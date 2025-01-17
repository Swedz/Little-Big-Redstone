package net.swedz.redstone_circuitry.gui.microchip.logic;

import net.minecraft.client.gui.GuiGraphics;
import net.swedz.redstone_circuitry.microchip.logic.Logic;

public abstract class LogicRenderer<L extends Logic>
{
	public LogicRenderer(LogicRendererProvider.Context context)
	{
	}
	
	public abstract void render(GuiGraphics graphics, L logic, int x, int y);
}
