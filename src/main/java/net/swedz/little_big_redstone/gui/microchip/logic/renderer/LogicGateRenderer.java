package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRendererProvider;
import net.swedz.little_big_redstone.helper.ColorConversions;
import net.swedz.little_big_redstone.microchip.logic.gate.LogicGate;

public final class LogicGateRenderer<G extends LogicGate<?, ?>> extends LogicRenderer<G>
{
	public LogicGateRenderer(LogicRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(Context context, GuiGraphics graphics, G component, int x, int y)
	{
		var size = component.size();
		
		this.renderAllPorts(context, graphics, x, y, component, 1, 1, 1);
		
		int argb = LBRColors.component(component.color().orElse(context.boardColor()));
		float red = ColorConversions.redFloat(argb);
		float green = ColorConversions.greenFloat(argb);
		float blue = ColorConversions.blueFloat(argb);
		this.renderBackground(graphics, x, y, size, red, green, blue);
		
		graphics.setColor(red, green, blue, 1);
		int centerX = x + size.centerX() - 8;
		int centerY = y + size.centerY() - 8;
		graphics.blit(LBR.id("textures/logic/%s.png".formatted(component.type().id())), centerX, centerY, 0, 0, 16, 16, 16, 16);
		graphics.setColor(1, 1, 1, 1);
	}
}
