package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientShaders;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRendererProvider;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;

public final class SimpleLogicRenderer<G extends LogicComponent<?, ?>> extends LogicRenderer<G>
{
	public SimpleLogicRenderer(LogicRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(Context context, TesseractGuiGraphics graphics, G component, int x, int y)
	{
		var size = component.size();
		
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
		int centerX = x + size.centerX() - 8;
		int centerY = y + size.centerY() - 8;
		graphics.blit(centerX, centerY, 0, 0, 16, 16, 16, 16);
		graphics.resetTextureShader();
		graphics.resetColor();
	}
}
