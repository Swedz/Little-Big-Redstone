package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRendererProvider;
import net.swedz.little_big_redstone.helper.GuiGraphicsHelper;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;

public final class SimpleLogicRenderer<G extends LogicComponent<?, ?>> extends LogicRenderer<G>
{
	public SimpleLogicRenderer(LogicRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(Context context, GuiGraphics graphics, G component, int x, int y)
	{
		var size = component.size();
		
		this.renderAllPorts(context, graphics, x, y, component, 1, 1, 1);
		this.renderBackground(context, graphics, x, y, component);
		
		GuiGraphicsHelper.setColor(graphics, context.foregroundColor());
		int centerX = x + size.centerX() - 8;
		int centerY = y + size.centerY() - 8;
		graphics.blit(context.getTexture("icon"), centerX, centerY, 0, 0, 16, 16, 16, 16);
		GuiGraphicsHelper.resetColor(graphics);
	}
}
