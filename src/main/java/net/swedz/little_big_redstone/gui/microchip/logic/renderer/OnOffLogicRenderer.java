package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;

public final class OnOffLogicRenderer<L extends LogicComponent<?, ?>> extends LogicRenderer<L>
{
	@Override
	public void render(Context context, GuiGraphicsExtractor graphics, L component, int x, int y)
	{
		var size = component.size();
		
		this.renderAllPorts(context, graphics, x, y, component);
		this.renderBackground(context, graphics, x, y, component);
		
		int centerX = x + size.centerX() - 8;
		int centerY = y + size.centerY() - 8;
		graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED,
				context.getTexture(component.output(0) > 0 ? "on" : "off"),
				centerX,
				centerY,
				16,
				16,
				context.foregroundColor()
		);
	}
}
