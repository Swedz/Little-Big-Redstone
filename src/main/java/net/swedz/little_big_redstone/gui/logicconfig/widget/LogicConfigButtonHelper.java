package net.swedz.little_big_redstone.gui.logicconfig.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientRenderPipelines;

public interface LogicConfigButtonHelper
{
	default void extractBackground(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color, boolean hovered)
	{
		float alpha = ((color >>> 24) & 0xFF) / 255f;
		float hoveredAlpha = hovered ? 0.4f : 0.2f;
		alpha *= hoveredAlpha;
		
		graphics.blitSprite(
				LBRClientRenderPipelines.LOGIC_SCANLINE,
				LBR.id("container/logic_config/button_background"),
				x,
				y,
				width,
				height,
				(Math.round(alpha * 255f) << 24) | (color & 0x00FFFFFF)
		);
	}
	
	default void extractBorder(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color)
	{
		graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED,
				LBR.id("container/logic_config/button_border"),
				x,
				y,
				width,
				height,
				color
		);
	}
}
