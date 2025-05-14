package net.swedz.little_big_redstone.item.logicarray.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.component.ItemContainerContents;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;
import net.swedz.little_big_redstone.item.logicarray.LogicArrayItem;

public final class LogicArrayClientTooltip implements ClientTooltipComponent
{
	private final ItemContainerContents storage;
	
	public LogicArrayClientTooltip(ItemContainerContents storage)
	{
		this.storage = storage;
	}
	
	@Override
	public int getHeight()
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
		return Math.min(LogicArrayItem.COLUMNS, storage.getSlots() + 1);
	}
	
	private int gridSizeY()
	{
		return Math.min(LogicArrayItem.ROWS, storage.getSlots() == 0 ? 0 : (int) Math.ceil(((double) storage.getSlots() + 1) / (double) this.gridSizeX()));
	}
	
	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics vanilla)
	{
		var graphics = new TesseractGuiGraphics(vanilla);
		
		if(storage.getSlots() != 0)
		{
			graphics.setTexture(LBR.id("textures/gui/slot_background.png"));
			graphics.nineSlice(x, y, this.backgroundWidth(), this.backgroundHeight(), 32, 32, 4);
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
	
	private void renderSlot(TesseractGuiGraphics graphics, Font font, int x, int y, int itemIndex)
	{
		graphics.setTexture(LBR.id("textures/gui/slot_atlas.png"));
		graphics.blit(x, y, 0, 0, 18, 18);
		
		if(itemIndex < storage.getSlots())
		{
			var stack = storage.getStackInSlot(itemIndex);
			graphics.renderItem(stack, x + 1, y + 1, itemIndex);
			graphics.renderItemDecorations(stack, x + 1, y + 1);
		}
	}
}
