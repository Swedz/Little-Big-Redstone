package net.swedz.little_big_redstone.item.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.component.ItemContainerContents;
import net.swedz.little_big_redstone.LBR;
import net.swedz.tesseract.api.Assert;
import net.swedz.tesseract.neoforge.helper.gui.ExtraGuiGraphics;

public final class ItemContainerContentsClientTooltip implements ClientTooltipComponent
{
	private final ItemContainerContents storage;
	
	private final int maxColumns, maxRows;
	
	private final boolean showExtraSlot;
	
	public ItemContainerContentsClientTooltip(ItemContainerContents storage, int maxColumns, int maxRows, boolean showExtraSlot)
	{
		Assert.that(maxColumns > 0, "Must have at least one column.");
		this.storage = storage;
		this.maxColumns = maxColumns;
		this.maxRows = maxRows;
		this.showExtraSlot = showExtraSlot;
	}
	
	@Override
	public int getHeight(Font font)
	{
		return storage.getSlots() == 0 ? 0 : (this.backgroundHeight() + 4);
	}
	
	@Override
	public int getWidth(Font font)
	{
		return storage.getSlots() == 0 ? 0 : this.backgroundWidth();
	}
	
	private int backgroundHeight()
	{
		return this.gridSizeY() * 18 + 3;
	}
	
	private int backgroundWidth()
	{
		return this.gridSizeX() * 18 + 2;
	}
	
	private int gridSizeX()
	{
		return Math.min(maxColumns, storage.getSlots() + (showExtraSlot ? 1 : 0));
	}
	
	private int gridSizeY()
	{
		int sizeY = storage.getSlots() == 0 ? 0 : (int) Math.ceil(((double) storage.getSlots() + (showExtraSlot ? 1 : 0)) / (double) this.gridSizeX());
		return maxRows > 0 ? Math.min(maxRows, sizeY) : sizeY;
	}
	
	@Override
	public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics)
	{
		if(storage.getSlots() != 0)
		{
			ExtraGuiGraphics.nineSlice(graphics, LBR.id("textures/gui/slot_background.png"), x, y, this.backgroundWidth(), this.backgroundHeight(), 32, 32, 4);
		}
		
		int index = 0;
		for(int gridY = 0; gridY < this.gridSizeY(); gridY++)
		{
			for(int gridX = 0; gridX < this.gridSizeX(); gridX++)
			{
				int posX = x + gridX * 18 + 1;
				int posY = y + gridY * 18 + 1;
				this.renderSlot(graphics, font, posX, posY, index++);
			}
		}
	}
	
	private void renderSlot(GuiGraphicsExtractor graphics, Font font, int x, int y, int itemIndex)
	{
		graphics.blit(LBR.id("textures/gui/slot_atlas.png"), x, y, 0, 0, 18, 18);
		
		if(itemIndex < storage.getSlots())
		{
			var stack = storage.getStackInSlot(itemIndex);
			graphics.item(stack, x + 1, y + 1, itemIndex);
			graphics.itemDecorations(font, stack, x + 1, y + 1);
		}
	}
}
