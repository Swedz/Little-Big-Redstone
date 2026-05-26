package net.swedz.little_big_redstone.gui.logicconfig.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.swedz.little_big_redstone.LBR;
import net.swedz.tesseract.neoforge.helper.gui.ExtraGuiGraphics;

public interface LogicConfigButtonHelper
{
	default void extractBackground(GuiGraphicsExtractor graphics, float partialTick, int x, int y, int width, int height, int color, boolean hovered)
	{
		float alpha = ((color >>> 24) & 0xFF) / 255f;
		float hoveredAlpha = hovered ? 0.4f : 0.2f;
		alpha *= hoveredAlpha;
		
		graphics.blit(
				RenderPipelines.GUI_TEXTURED,
				LBR.id("textures/gui/container/logic_config/button_scanline.png"),
				x,
				y,
				0,
				0,
				width,
				height,
				18,
				18,
				(Math.round(alpha * 255f) << 24) | (color & 0x00FFFFFF)
		);
		/*graphics.setTexture(LBR.id("textures/gui/container/logic_config/button_scanline.png"));
		graphics.setColor((hovered ? 0x64000000 : 0x32000000) | (color & 0x00FFFFFF));
		graphics.blit(x, y, 0, (Util.getMillis() + partialTick) / 300, width, height, 18, 18);
		graphics.resetColor();*/
	}
	
	default void extractBorder(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color)
	{
		ExtraGuiGraphics.nineSlice(
				graphics,
				LBR.id("textures/gui/container/logic_config/button.png"),
				color,
				0xFFFFFFFF,
				x,
				y,
				width,
				height,
				18,
				18,
				8
		);
		/*graphics.setTexture(LBR.id("textures/gui/container/logic_config/button.png"));
		graphics.setColor(color);
		graphics.nineSlice(x, y, width, height, 18, 18, 8);
		graphics.resetColor();*/
	}
}
