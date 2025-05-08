package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientShaders;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRendererProvider;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;
import net.swedz.little_big_redstone.microchip.logic.sequencer.LogicSequencer;

public final class SequencerRenderer extends LogicRenderer<LogicSequencer>
{
	public SequencerRenderer(LogicRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(Context context, TesseractGuiGraphics graphics, LogicSequencer component, int x, int y)
	{
		var size = component.size();
		
		this.renderAllPorts(context, graphics, x, y, component, 1, 1, 1);
		this.renderBackground(context, graphics, x, y, component);
		
		int fillWidth = (int) ((size.widthPixels() - 4) * component.processedPercentage());
		int fillHeight = size.heightPixels() - 4;
		
		graphics.setColor(context.foregroundColor());
		graphics.setTextureShader(
				LBRClientShaders::logicScanline,
				(shader) -> shader.getUniform("LogicUV").set((float) fillWidth, (float) fillHeight)
		);
		graphics.setTextures(
				context.getTexture("progress"),
				LBR.id("textures/logic/scanline.png")
		);
		graphics.blit(x + 2, y + 2, 0, 0, fillWidth, fillHeight, 12, 12);
		graphics.resetTextureShader();
		graphics.resetColor();
	}
}
