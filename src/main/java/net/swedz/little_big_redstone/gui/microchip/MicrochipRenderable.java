package net.swedz.little_big_redstone.gui.microchip;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.helper.GuiGraphicsHelper;
import net.swedz.little_big_redstone.microchip.LogicIndex;
import net.swedz.little_big_redstone.microchip.LogicSelectedPort;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.network.packet.PlaceTakeLogicPacket;

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
				var entry = microchip.findAt(x, y);
				if(entry != null)
				{
					// TODO pop all selected ports that are from this logic component (including other players)
					microchip.remove(entry);
					menu.setCarried(entry.toStack());
					new PlaceTakeLogicPacket(menu.containerId, x, y, false).sendToServer();
				}
				else
				{
					if(selectedPort != null)
					{
						var inputPort = microchip.findAtPort(x, y, true);
						if(inputPort != null)
						{
							LBR.LOGGER.info("inserting output from {}:{} to {}:{}", selectedPort.entry().slot(), selectedPort.portIndex(), inputPort.entry().slot(), inputPort.portIndex());
							selectedPort.entry().outputPorts().add(selectedPort.portIndex(), inputPort);
							microchip.markDirty();
							// TODO inform the server
						}
						selectedPort = null;
					}
					else
					{
						// TODO try to grab hovered wire first
						
						var outputPort = microchip.findAtPort(x, y, false);
						if(outputPort != null)
						{
							selectedPort = outputPort;
						}
					}
				}
			}
			else if(button == InputConstants.MOUSE_BUTTON_RIGHT)
			{
				// TODO open edit side menu
			}
		}
		else if(carried.has(LBRComponents.LOGIC) && button == InputConstants.MOUSE_BUTTON_LEFT)
		{
			var logic = carried.get(LBRComponents.LOGIC);
			int placeX = logic.size().topLeftCornerX(x);
			int placeY = logic.size().topLeftCornerY(y);
			if(microchip.canFit(placeX, placeY, logic))
			{
				microchip.add(placeX, placeY, logic);
				menu.setCarried(ItemStack.EMPTY);
				new PlaceTakeLogicPacket(menu.containerId, placeX, placeY, true).sendToServer();
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
		// TODO render the wire connections, this should be cached and recalculated when something is moved...
	}
	
	private void renderLogic(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		var context = new LogicRenderer.Context(false);
		for(LogicIndex entry : microchip.values())
		{
			LogicRenderers.render(context, graphics, entry.logic(), entry.x(), entry.y());
		}
	}
	
	private void renderShadowBg(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, int padding)
	{
		for(int p = padding; p >= 1; p--)
		{
			graphics.fill(-p, -p, width + p, height + p, 0x40000000);
		}
		
		graphics.enableScissor(x - padding, y - padding, x + width + padding, y + height + padding);
		GuiGraphicsHelper.blit(graphics, SHADOW_HOVER_OVERLAY, this.toLocalX(mouseX) - 64, this.toLocalY(mouseY) - 64, 128, 128, 0, 0, 64, 64, 64, 64, 1, 1, 1, 0.11f);
		graphics.disableScissor();
	}
	
	private void renderCircuitBg(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		this.renderShadowBg(graphics, mouseX, mouseY, partialTicks, 3);
		
		graphics.setColor(1, 0.5f, 0.5f, 1);
		graphics.blit(CIRCUIT_BACKGROUND, 0, 0, 0, 0, width, height, 64, 64);
		graphics.setColor(1, 1, 1, 1);
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
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
