package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRendererProvider;
import net.swedz.little_big_redstone.helper.GuiGraphicsHelper;
import net.swedz.little_big_redstone.microchip.logic.LogicGridSize;
import net.swedz.little_big_redstone.microchip.logic.io.LogicIO;

public final class IORenderer extends LogicRenderer<LogicIO>
{
	public IORenderer(LogicRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(Context context, GuiGraphics graphics, LogicIO component, int x, int y)
	{
		this.renderAllPorts(context, graphics, x, y, component, 1, 1, 1);
		
		this.renderBackground(
				graphics,
				context.getTexture("background"),
				context.getTexture("border"),
				x, y, LogicGridSize.SINGLE,
				context.foregroundColor(), context.backgroundColor()
		);
		
		GuiGraphicsHelper.setColor(graphics, context.foregroundColor());
		graphics.blit(context.getTexture(component.config().input ? "input" : "output"), x, y, 0, 0, 16, 16, 16, 16);
		GuiGraphicsHelper.resetColor(graphics);
		
		if(!component.config().isValid())
		{
			this.renderInvalidOverlay(graphics, x, y, component.size());
		}
	}
}
