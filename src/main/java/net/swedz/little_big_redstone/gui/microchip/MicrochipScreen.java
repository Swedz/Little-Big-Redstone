package net.swedz.little_big_redstone.gui.microchip;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.microchip.Microchip;

public final class MicrochipScreen extends AbstractContainerScreen<MicrochipMenu>
{
	private static final ResourceLocation INVENTORY_BACKGROUND = LBR.id("textures/gui/container/microchip/inventory_background.png");
	
	public MicrochipScreen(MicrochipMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		
		imageWidth = 256;
		imageHeight = 256 - (12 * 2);
	}
	
	private boolean isWithinBoard(int x, int y)
	{
		return Microchip.BOUNDS.contains(x, y);
	}
	
	@Override
	protected void init()
	{
		super.init();
		
		var bounds = Microchip.BOUNDS;
		this.addRenderableWidget(new MicrochipRenderable(bounds.minX() + leftPos, bounds.minY() + topPos, bounds.width(), bounds.height(), this));
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
	{
		super.render(graphics, mouseX, mouseY, partialTick);
		
		this.renderTooltip(graphics, mouseX, mouseY);
	}
	
	@Override
	public void renderFloatingItem(GuiGraphics graphics, ItemStack stack, int x, int y, String text)
	{
		int mouseX = x + 8;
		int mouseY = y + 8;
		if(stack.has(LBRComponents.LOGIC) && this.isWithinBoard(mouseX, mouseY))
		{
			var logic = stack.get(LBRComponents.LOGIC);
			int logicX = logic.size().topLeftCornerX(mouseX);
			int logicY = logic.size().topLeftCornerY(mouseY);
			var context = new LogicRenderer.Context(true);
			LogicRenderers.render(context, graphics, logic, logicX, logicY);
		}
		else
		{
			super.renderFloatingItem(graphics, stack, x, y, text);
		}
	}
	
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
		// Dont render the "Microchip" and "Inventory" labels
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
	{
		graphics.blit(INVENTORY_BACKGROUND, leftPos, topPos + imageHeight - 90, 0, 0, 256, 90);
	}
}
