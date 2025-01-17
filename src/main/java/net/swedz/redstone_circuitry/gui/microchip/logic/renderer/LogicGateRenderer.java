package net.swedz.redstone_circuitry.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.swedz.redstone_circuitry.RedstoneCircuitry;
import net.swedz.redstone_circuitry.gui.microchip.logic.LogicRenderer;
import net.swedz.redstone_circuitry.gui.microchip.logic.LogicRendererProvider;
import net.swedz.redstone_circuitry.microchip.logic.gate.LogicGate;

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
		
		this.renderBackground(graphics, BACKGROUND, x, y, size.width(), size.height(), 1, 1, 1);
		this.renderBackground(graphics, BACKGROUND_OVERLAY, x, y, size.width(), size.height(), 1, 1, 1);
		
		int centerX = x + ((size.width() * 16) / 2) - 8;
		int centerY = y + ((size.height() * 16) / 2) - 8;
		graphics.blit(RedstoneCircuitry.id("textures/logic/%s.png".formatted(logic.type().id())), centerX, centerY, 0, 0, 16, 16, 16, 16);
	}
}
