package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.microchip.object.logic.io.LogicIO;
import net.swedz.little_big_redstone.microchip.object.logic.io.LogicIOConfig;

public final class IORenderer extends LogicRenderer<LogicIO, LogicIOConfig>
{
	@Override
	public void render(Context context, GuiGraphicsExtractor graphics, LogicIO component, int x, int y)
	{
		this.renderAllPorts(context, graphics, x, y, component);
		this.renderBackground(context, graphics, x, y, component);
		
		graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED,
				context.getTexture(component.config().input() ? "input" : "output"),
				x,
				y,
				16,
				16,
				context.foregroundColor()
		);
		
		if(!component.isConfigValid())
		{
			this.renderInvalidOverlay(graphics, x, y, component.size());
		}
	}
}
