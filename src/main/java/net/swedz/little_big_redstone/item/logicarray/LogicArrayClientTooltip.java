package net.swedz.little_big_redstone.item.logicarray;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.gui.logicarray.LogicArrayMenu;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;

public final class LogicArrayClientTooltip implements ClientTooltipComponent
{
	private static final ResourceLocation BACKGROUND = LBR.id("textures/gui/container/logic_array/background.png");
	private static final ResourceLocation SLOT_ATLAS = LBR.id("textures/gui/container/logic_array/slot_atlas.png");
	
	private final LogicArrayStorage storage;
	
	public LogicArrayClientTooltip(LogicArrayStorage storage)
	{
		this.storage = storage;
	}
	
	@Override
	public int getHeight()
	{
		return storage.isEmpty() ? 0 : (this.backgroundHeight() + 4);
	}
	
	@Override
	public int getWidth(Font font)
	{
		return storage.isEmpty() ? 0 : this.backgroundWidth();
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
		return Math.min(LogicArrayMenu.COLUMNS, storage.getSlots());
	}
	
	private int gridSizeY()
	{
		return Math.min(LogicArrayMenu.ROWS, storage.isEmpty() ? 0 : (int) Math.ceil(((double) storage.getSlots() + 1) / (double) this.gridSizeX()));
	}
	
	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics vanilla)
	{
		var graphics = new TesseractGuiGraphics(vanilla);
		
		if(!storage.isEmpty())
		{
			graphics.setTexture(BACKGROUND);
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
		graphics.setTexture(SLOT_ATLAS);
		graphics.blit(x, y, 0, 0, 18, 18);
		
		if(itemIndex < storage.getSlots())
		{
			var stack = storage.getStackInSlot(itemIndex);
			graphics.vanilla().renderItem(stack, x + 1, y + 1, itemIndex);
			graphics.vanilla().renderItemDecorations(font, stack, x + 1, y + 1);
		}
	}
}
