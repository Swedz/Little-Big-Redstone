package net.swedz.little_big_redstone.gui.microchip.logic;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.helper.GuiGraphicsHelper;
import net.swedz.little_big_redstone.microchip.logic.Logic;
import net.swedz.little_big_redstone.microchip.logic.LogicGridSize;

public abstract class LogicRenderer<L extends Logic>
{
	public static final ResourceLocation BACKGROUND         = LBR.id("textures/logic/background.png");
	public static final ResourceLocation BACKGROUND_OVERLAY = LBR.id("textures/logic/background_overlay.png");
	
	public LogicRenderer(LogicRendererProvider.Context context)
	{
	}
	
	public abstract void render(GuiGraphics graphics, L logic, int x, int y);
	
	protected void renderGridBlock(GuiGraphics graphics, ResourceLocation texture, int x, int y, LogicGridSize size, float red, float green, float blue)
	{
		graphics.setColor(red, green, blue, 1);
		
		int width = size.width();
		int height = size.height();
		
		if(width == 1 && height == 1)
		{
			graphics.blit(texture, x, y, 0, 0, 16, 16, 16, 16);
		}
		else
		{
			GuiGraphicsHelper.nineSlice(graphics, texture, x, y, width * 16, height * 16, 16, 16, 3);
		}
		
		graphics.setColor(1, 1, 1, 1);
	}
	
	protected void renderBackground(GuiGraphics graphics, int x, int y, LogicGridSize size, float red, float green, float blue)
	{
		this.renderGridBlock(graphics, BACKGROUND, x, y, size, red, green, blue);
		this.renderGridBlock(graphics, BACKGROUND_OVERLAY, x, y, size, 1, 1, 1);
	}
}
