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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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

import java.util.List;
import java.util.function.Consumer;

public final class MicrochipRenderable implements GuiEventListener, Renderable, NarratableEntry
{
	private static final ResourceLocation SHADOW_HOVER_OVERLAY = LBR.id("textures/gui/container/microchip/shadow_hover_overlay.png");
	private static final ResourceLocation CIRCUIT_BACKGROUND   = LBR.id("textures/gui/container/microchip/circuit_background.png");
	
	private final int x, y, width, height;
	
	private final MicrochipScreen screen;
	
	private final Microchip microchip;
	
	private LogicSelectedPort selectedPort;
	
	public MicrochipRenderable(int x, int y, MicrochipScreen screen)
	{
		this.microchip = screen.getMenu().microchip();
		var bounds = microchip.size().bounds();
		this.x = x + microchip.size().scale(bounds.minX());
		this.y = y + microchip.size().scale(bounds.minY());
		this.width = bounds.width();
		this.height = bounds.height();
		this.screen = screen;
	}
	
	public boolean hasSelectedPort()
	{
		return selectedPort != null;
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
		else if(carried.has(LBRComponents.LOGIC) && button == InputConstants.MOUSE_BUTTON_LEFT)
		{
			var component = carried.get(LBRComponents.LOGIC);
			int placeX = component.size().topLeftCornerX(x);
			int placeY = component.size().topLeftCornerY(y);
			
			if(microchip.size().bounds().normalize().contains(component.size().toBounds(placeX, placeY)) && microchip.components().add(placeX, placeY, component))
			{
				microchip.markDirty();
				carried.shrink(1);
				new PlaceTakeMicrochipLogicPacket(menu.containerId, placeX, placeY, true).sendToServer();
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
			return this.mouseClickedOnBoard(microchip.size().boardX(this.toLocalX(mouseX)), microchip.size().boardY(this.toLocalY(mouseY)), button);
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
	
	private void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		if(this.isMouseOver(mouseX, mouseY))
		{
			var carried = screen.getMenu().getCarried();
			if(carried.isEmpty())
			{
				var hovered = microchip.components().findAt(microchip.size().boardX(this.toLocalX(mouseX)), microchip.size().boardY(this.toLocalY(mouseY)));
				if(hovered != null)
				{
					var component = hovered.component();
					component.type().tooltip(component, true).ifPresent((Consumer<List<Component>>) (lines) ->
					{
						lines.addFirst(component.type().displayName().withStyle(Style.EMPTY.withUnderlined(true)));
						graphics.renderComponentTooltip(Minecraft.getInstance().font, lines, mouseX, mouseY);
					});
				}
			}
		}
	}
	
	private void renderWires(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		// TODO redo this to use a pathing algorithm like A*: render the wire connections, this should be cached and recalculated when something is moved...
		
		for(Wire wire : microchip.wires())
		{
			LogicEntry outputLogic = microchip.components().get(wire.output().slot());
			LogicEntry inputLogic = microchip.components().get(wire.input().slot());
			
			int x1 = microchip.size().scale(outputLogic.x() + outputLogic.component().size().widthPixels() + 3) + x;
			int y1 = microchip.size().scale(outputLogic.component().size().portTopLeftCornerY(outputLogic.y(), false, wire.output().index(), outputLogic.component().outputs()) + 8) + y;
			int x2 = microchip.size().scale(inputLogic.x()) + x;
			int y2 = microchip.size().scale(inputLogic.component().size().portTopLeftCornerY(inputLogic.y(), true, wire.input().index(), inputLogic.component().inputs()) + 8) + y;
			
			boolean powered = outputLogic.component().output(wire.output().index());
			
			this.renderWire(graphics, partialTicks, x1, y1, x2, y2, powered ? 0xFFFFFFFF : 0xFF000000);
		}
		
		if(selectedPort != null)
		{
			int x1 = microchip.size().scale(selectedPort.entry().x() + selectedPort.entry().component().size().widthPixels() + 3) + x;
			int y1 = microchip.size().scale(selectedPort.entry().component().size().portTopLeftCornerY(selectedPort.entry().y(), false, selectedPort.portIndex(), selectedPort.entry().component().outputs()) + 8) + y;
			int x2 = mouseX;
			int y2 = mouseY;
			
			boolean powered = selectedPort.entry().component().output(selectedPort.portIndex());
			
			this.renderWire(graphics, partialTicks, x1, y1, x2, y2, powered ? 0xFFFFFFFF : 0xFF000000);
		}
	}
	
	private void renderWire(GuiGraphics graphics, float partialTicks, int x1, int y1, int x2, int y2, int color)
	{
		graphics.pose().pushPose();
		var buffer = graphics.bufferSource().getBuffer(RenderType.lines());
		buffer.addVertex(x1, y1, 0).setColor(color).setNormal(1, 1, 0);
		buffer.addVertex(x2, y2, 0).setColor(color).setNormal(1, 1, 0);
		graphics.bufferSource().endBatch(RenderType.lines());
		graphics.pose().popPose();
	}
	
	private void renderLogic(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		var context = new LogicRenderer.Context(false, this.hasSelectedPort(), screen.getMenu().getCarried().is(LBRTags.Items.MICROCHIP_WIRE));
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
		graphics.pose().scale(microchip.size().scale(), microchip.size().scale(), microchip.size().scale());
		
		this.renderCircuitBg(graphics, mouseX, mouseY, partialTicks);
		this.renderLogic(graphics, mouseX, mouseY, partialTicks);
		this.renderWires(graphics, mouseX, mouseY, partialTicks);
		
		graphics.pose().popPose();
		
		this.renderTooltip(graphics, mouseX, mouseY, partialTicks);
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
		return new ScreenRectangle(x, y, microchip.size().scale(width), microchip.size().scale(height));
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
