package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientShaders;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRendererProvider;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;
import net.swedz.little_big_redstone.microchip.logic.reader.LogicReader;

public final class ReaderRenderer extends LogicRenderer<LogicReader>
{
	public ReaderRenderer(LogicRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(Context context, TesseractGuiGraphics graphics, LogicReader component, int x, int y)
	{
		this.renderAllPorts(context, graphics, x, y, component, 1, 1, 1);
		this.renderBackground(context, graphics, x, y, component);
		
		graphics.setColor(context.foregroundColor());
		graphics.setTextureShader(
				LBRClientShaders::logicScanline,
				(shader) -> shader.getUniform("LogicUV").set(16f, 16f)
		);
		graphics.setTextures(
				context.getTexture("icon"),
				LBR.id("textures/logic/scanline.png")
		);
		graphics.blit(x, y, 0, 0, 16, 16, 16, 16);
		graphics.resetTextureShader();
		graphics.resetColor();
	}
}
