package net.swedz.little_big_redstone.gui.microchip;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.microchip.Microchip;

public final class MicrochipScreen extends AbstractContainerScreen<MicrochipMenu>
{
	private static final ResourceLocation INVENTORY_BACKGROUND = LBR.id("textures/gui/container/microchip/inventory_background.png");
	
	private final Microchip microchip;
	
	private int boardX, boardY, boardWidth, boardHeight;
	
	public MicrochipScreen(MicrochipMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		
		imageWidth = 256;
		imageHeight = 256 - (12 * 2);
		
		boardX = 0;
		boardY = 0;
		boardWidth = imageWidth;
		boardHeight = imageHeight - 90 - 4;
		
		microchip = menu.microchip();
	}
	
	private boolean isWithinBoard(int x, int y)
	{
		return x >= boardX && x < boardX + boardWidth &&
			   y >= boardY && y < boardY + boardHeight;
	}
	
	@Override
	protected void init()
	{
		super.init();
		
		this.addRenderableWidget(new MicrochipRenderable(boardX + leftPos, boardY + topPos, boardWidth, boardHeight, microchip));
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
		if(stack.has(LBRComponents.LOGIC) && this.isWithinBoard(x + 8, y + 8))
		{
			var logic = stack.get(LBRComponents.LOGIC);
			LogicRenderers.render(graphics, logic, x, y);
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
