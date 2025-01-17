package net.swedz.little_big_redstone.gui.microchip.logic;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.logic.Logic;

public abstract class LogicRenderer<L extends Logic>
{
	public static final ResourceLocation BACKGROUND         = LBR.id("textures/logic/background.png");
	public static final ResourceLocation BACKGROUND_OVERLAY = LBR.id("textures/logic/background_overlay.png");
	
	public LogicRenderer(LogicRendererProvider.Context context)
	{
	}
	
	public abstract void render(GuiGraphics graphics, L logic, int x, int y);
	
	protected void renderBackground(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, float red, float green, float blue)
	{
		graphics.setColor(red, green, blue, 1);
		
		if(width == 1 && height == 1)
		{
			graphics.blit(texture, x, y, 0, 0, 16, 16, 16, 16);
		}
		else
		{
			int widthPixels = width * 16;
			int heightPixels = height * 16;
			
			graphics.blit(texture, x, y, 0, 0, 1, 1, 16, 16);
			graphics.blit(texture, x + widthPixels - 1, y, 15, 0, 1, 1, 16, 16);
			graphics.blit(texture, x + widthPixels - 1, y + heightPixels - 1, 15, 15, 1, 1, 16, 16);
			graphics.blit(texture, x, y + heightPixels - 1, 0, 15, 1, 1, 16, 16);
			
			graphics.blit(texture, x + 1, y, widthPixels - 2, 15, 1, 0, 14, 15, 16, 16);
			graphics.blit(texture, x + widthPixels - 15, y + 1, 15, heightPixels - 2, 1, 1, 15, 14, 16, 16);
			graphics.blit(texture, x + 1, y + heightPixels - 15, widthPixels - 2, 15, 1, 1, 14, 15, 16, 16);
			graphics.blit(texture, x, y + 1, 15, heightPixels - 2, 0, 1, 15, 14, 16, 16);
		}
		
		graphics.setColor(1, 1, 1, 1);
	}
}
