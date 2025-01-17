package net.swedz.redstone_circuitry.gui.microchip.logic;

import net.swedz.redstone_circuitry.microchip.logic.Logic;

public interface LogicRendererProvider<L extends Logic>
{
	LogicRenderer<L> create(Context context);
	
	record Context()
	{
	}
}
