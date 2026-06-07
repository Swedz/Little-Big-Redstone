package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.swedz.little_big_redstone.LBRClientRenderPipelines;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;

public final class SimpleLogicRenderer<G extends LogicComponent<G, C>, C extends LogicConfig> extends LogicRenderer<G, C>
{
	@Override
	public void render(Context context, GuiGraphicsExtractor graphics, G component, int x, int y)
	{
		var size = component.size();
		
		this.renderAllPorts(context, graphics, x, y, component);
		this.renderBackground(context, graphics, x, y, component);
		
		int centerX = x + size.centerX() - 8;
		int centerY = y + size.centerY() - 8;
		graphics.blitSprite(
				LBRClientRenderPipelines.LOGIC_SCANLINE,
				context.getTexture("icon"),
				centerX,
				centerY,
				16,
				16,
				context.foregroundColor()
		);
	}
}
