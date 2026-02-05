package net.swedz.little_big_redstone.gui.logicconfig.widget;

import net.minecraft.Util;
import net.swedz.little_big_redstone.LBR;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

public interface LogicConfigButtonHelper
{
	default void renderBackground(TesseractGuiGraphics graphics, float partialTick, int x, int y, int width, int height, int color, boolean hovered)
	{
		graphics.setTexture(LBR.id("textures/gui/container/logic_config/button_scanline.png"));
		graphics.setColor((hovered ? 0x64000000 : 0x32000000) | (color & 0x00FFFFFF));
		graphics.blit(x, y, 0, (Util.getMillis() + partialTick) / 300, width, height, 18, 18);
		graphics.resetColor();
	}
	
	default void renderBorder(TesseractGuiGraphics graphics, int x, int y, int width, int height, int color)
	{
		graphics.setTexture(LBR.id("textures/gui/container/logic_config/button.png"));
		graphics.setColor(color);
		graphics.nineSlice(x, y, width, height, 18, 18, 8);
		graphics.resetColor();
	}
}
