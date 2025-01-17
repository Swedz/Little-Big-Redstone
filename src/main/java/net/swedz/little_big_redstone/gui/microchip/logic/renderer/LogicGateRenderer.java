package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRendererProvider;
import net.swedz.little_big_redstone.microchip.logic.gate.LogicGate;

public final class LogicGateRenderer<G extends LogicGate> extends LogicRenderer<G>
{
	public LogicGateRenderer(LogicRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(GuiGraphics graphics, LogicGate logic, int x, int y)
	{
		var size = logic.size();
		
		x -= ((size.width() - 1) * 16) / 2;
		y -= ((size.height() - 1) * 16) / 2;
		
		this.renderBackground(graphics, x, y, size, 1, 1, 1);
		
		int centerX = x + ((size.width() * 16) / 2) - 8;
		int centerY = y + ((size.height() * 16) / 2) - 8;
		graphics.blit(LBR.id("textures/logic/%s.png".formatted(logic.type().id())), centerX, centerY, 0, 0, 16, 16, 16, 16);
	}
}
