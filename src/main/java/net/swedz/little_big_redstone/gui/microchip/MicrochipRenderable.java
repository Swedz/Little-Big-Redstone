package net.swedz.little_big_redstone.gui.microchip;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.microchip.LogicEntry;
import net.swedz.little_big_redstone.microchip.LogicSelectedPort;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.wire.Wire;
import net.swedz.little_big_redstone.network.packet.CreateMicrochipWirePacket;
import net.swedz.little_big_redstone.network.packet.PlaceTakeMicrochipLogicPacket;

public final class MicrochipRenderable implements GuiEventListener, Renderable, NarratableEntry
{
	private static final ResourceLocation SHADOW_HOVER_OVERLAY = LBR.id("textures/gui/container/microchip/shadow_hover_overlay.png");
	private static final ResourceLocation CIRCUIT_BACKGROUND   = LBR.id("textures/gui/container/microchip/circuit_background.png");
	
	private final int x, y, width, height;
	
	private final MicrochipScreen screen;
	
	private final Microchip microchip;
	
	private LogicSelectedPort selectedPort;
	
	public MicrochipRenderable(int x, int y, int width, int height, MicrochipScreen screen)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.screen = screen;
		this.microchip = screen.getMenu().microchip();
	}
	
	private boolean mouseClickedOnBoard(int x, int y, int button)
	{
		var menu = screen.getMenu();
		var carried = menu.getCarried();
		if(carried.isEmpty())
		{
			if(button == InputConstants.MOUSE_BUTTON_LEFT)
			{
				var entry = microchip.components().findAt(x, y);
				if(entry != null)
				{
					microchip.components().remove(entry);
					microchip.markDirty();
					menu.setCarried(entry.toStack());
					new PlaceTakeMicrochipLogicPacket(menu.containerId, x, y, false).sendToServer();
				}
			}
			else if(button == InputConstants.MOUSE_BUTTON_RIGHT)
			{
				// TODO open edit side menu
			}
		}
		else if(carried.has(LBRComponents.LOGIC) && button == InputConstants.MOUSE_BUTTON_LEFT)
		{
			var component = carried.get(LBRComponents.LOGIC);
			int placeX = component.size().topLeftCornerX(x);
			int placeY = component.size().topLeftCornerY(y);
			
			if(Microchip.BOUNDS.normalize().contains(component.size().toBounds(placeX, placeY)) && microchip.components().add(placeX, placeY, component))
			{
				microchip.markDirty();
				carried.shrink(1);
				new PlaceTakeMicrochipLogicPacket(menu.containerId, placeX, placeY, true).sendToServer();
			}
		}
		else if(carried.is(LBRTags.Items.MICROCHIP_WIRE) && button == InputConstants.MOUSE_BUTTON_LEFT)
		{
			if(selectedPort != null)
			{
				var inputPort = microchip.components().findPortAt(x, y, true);
				if(inputPort != null && microchip.wires().add(selectedPort, inputPort))
				{
					microchip.markDirty();
					carried.shrink(1);
					new CreateMicrochipWirePacket(menu.containerId, selectedPort, inputPort).sendToServer();
				}
				selectedPort = null;
			}
			else
			{
				// TODO try to grab hovered wire first
				
				var outputPort = microchip.components().findPortAt(x, y, false);
				if(outputPort != null)
				{
					selectedPort = outputPort;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean mouseClicked(double mx, double my, int button)
	{
		int mouseX = (int) mx;
		int mouseY = (int) my;
		if(this.isMouseOver(mouseX, mouseY))
		{
			return this.mouseClickedOnBoard(this.toLocalX(mouseX), this.toLocalY(mouseY), button);
		}
		else
		{
			if(selectedPort != null)
			{
				selectedPort = null;
				return true;
			}
			return false;
		}
	}
	
	private void renderWires(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		// TODO redo this to use a pathing algorithm like A*: render the wire connections, this should be cached and recalculated when something is moved...
		for(Wire wire : microchip.wires())
		{
			LogicEntry outputLogic = microchip.components().get(wire.output().slot());
			LogicEntry inputLogic = microchip.components().get(wire.input().slot());
			
			int x1 = outputLogic.x() + outputLogic.component().size().widthPixels() + x + 3;
			int y1 = outputLogic.component().size().portTopLeftCornerY(outputLogic.y(), false, wire.output().index(), outputLogic.component().outputs()) + 8 + y;
			int x2 = inputLogic.x() + x;
			int y2 = inputLogic.component().size().portTopLeftCornerY(inputLogic.y(), true, wire.input().index(), inputLogic.component().inputs()) + 8 + y;
			
			boolean powered = outputLogic.component().output(wire.output().index());
			int color = powered ? 0xFFFFFFFF : 0xFF000000;
			
			graphics.pose().pushPose();
			var buffer = graphics.bufferSource().getBuffer(RenderType.lines());
			buffer.addVertex(x1, y1, 0).setColor(color).setNormal(1, 1, 0);
			buffer.addVertex(x2, y2, 0).setColor(color).setNormal(1, 1, 0);
			graphics.bufferSource().endBatch(RenderType.lines());
			graphics.pose().popPose();
		}
	}
	
	private void renderLogic(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		var context = new LogicRenderer.Context(false);
		int traversalIndex = 0;
		for(var entry : microchip.components().traversal())
		{
			LogicRenderers.render(context, graphics, entry.component(), entry.x(), entry.y());
			graphics.drawString(Minecraft.getInstance().font, entry.slot() + " (" + traversalIndex + ")", entry.x(), entry.y() - 8, 0xFFFFFF);
			traversalIndex++;
		}
	}
	
	private void renderCircuitBg(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		graphics.setColor(1, 0.5f, 0.5f, 1);
		graphics.blit(CIRCUIT_BACKGROUND, 0, 0, 0, 0, width, height, 64, 64);
		graphics.setColor(1, 1, 1, 1);
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		// TODO perform this check when the change happens instead of every frame
		if(selectedPort != null)
		{
			var component = microchip.components().get(selectedPort.entry().slot());
			if(component == null || selectedPort.portIndex() >= component.component().outputs())
			{
				selectedPort = null;
				LBR.LOGGER.info("Cleared selected port because it doesn't exist anymore");
			}
		}
		
		graphics.pose().pushPose();
		graphics.pose().translate(x, y, 0);
		
		this.renderCircuitBg(graphics, mouseX, mouseY, partialTicks);
		this.renderLogic(graphics, mouseX, mouseY, partialTicks);
		this.renderWires(graphics, mouseX, mouseY, partialTicks);
		
		graphics.pose().popPose();
	}
	
	@Override
	public void setFocused(boolean focused)
	{
	}
	
	@Override
	public boolean isFocused()
	{
		return false;
	}
	
	@Override
	public NarrationPriority narrationPriority()
	{
		return NarrationPriority.NONE;
	}
	
	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput)
	{
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY)
	{
		return this.getRectangle().containsPoint((int) mouseX, (int) mouseY);
	}
	
	@Override
	public ScreenRectangle getRectangle()
	{
		return new ScreenRectangle(x, y, width, height);
	}
	
	private int toLocalX(int x)
	{
		return x - this.x;
	}
	
	private int toLocalY(int y)
	{
		return y - this.y;
	}
}
