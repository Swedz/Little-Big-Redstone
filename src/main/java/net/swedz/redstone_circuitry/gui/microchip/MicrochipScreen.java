package net.swedz.redstone_circuitry.gui.microchip;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.swedz.redstone_circuitry.RedstoneCircuitry;

public final class MicrochipScreen extends AbstractContainerScreen<MicrochipMenu>
{
	public MicrochipScreen(MicrochipMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		
		imageWidth = 256;
		imageHeight = 256;
	}
	
	@Override
	protected void init()
	{
		super.init();
		
		// TODO
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
	{
		super.render(graphics, mouseX, mouseY, partialTick);
		
		// TODO
	}
	
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
	}
	
	private void renderCircuitBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, int originX, int originY, int boardWidth, int boardHeight)
	{
		graphics.setColor(1, 0.5f, 0.5f, 1);
		
		int blockSize = 64;
		
		int widthBlocks = boardWidth / blockSize;
		int heightBlocks = boardHeight / blockSize;
		
		for(int column = 0; column <= widthBlocks; column++)
		{
			for(int row = 0; row <= heightBlocks; row++)
			{
				int x = originX + (column * blockSize);
				int y = originY + (row * blockSize);
				int width = column == widthBlocks ? boardWidth % blockSize : blockSize;
				int height = row == heightBlocks ? boardHeight % blockSize : blockSize;
				graphics.blit(RedstoneCircuitry.id("textures/gui/container/microchip_background.png"), x, y, 0, 0, width, height, blockSize, blockSize);
			}
		}
		
		graphics.setColor(1, 1, 1, 1);
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
	{
		graphics.pose().pushPose();
		graphics.pose().translate(leftPos, topPos, 0);
		
		this.renderCircuitBg(graphics, partialTick, mouseX, mouseY, 0, 8 + 4, imageWidth, imageHeight - 90 - 12 - 4 - 12);
		
		graphics.blit(RedstoneCircuitry.id("textures/gui/container/microchip_bottom.png"), 0, imageHeight - 90 - 12, 0, 0, 256, 90);
		
		graphics.pose().popPose();
	}
}
