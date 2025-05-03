package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRendererProvider;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;
import net.swedz.little_big_redstone.microchip.logic.io.LogicIO;

public final class IORenderer extends LogicRenderer<LogicIO>
{
	public IORenderer(LogicRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(Context context, TesseractGuiGraphics graphics, LogicIO component, int x, int y)
	{
		this.renderAllPorts(context, graphics, x, y, component, 1, 1, 1);
		this.renderBackground(context, graphics, x, y, component);
		
		graphics.setColor(context.foregroundColor());
		graphics.setTexture(context.getTexture(component.config().input ? "input" : "output"));
		graphics.blit(x, y, 0, 0, 16, 16, 16, 16);
		graphics.resetColor();
		
		if(!component.config().isValid())
		{
			this.renderInvalidOverlay(graphics, x, y, component.size());
		}
	}
}
