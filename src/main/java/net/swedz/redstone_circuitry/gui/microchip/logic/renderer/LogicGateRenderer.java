package net.swedz.redstone_circuitry.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphics;
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
		// TODO
	}
}
