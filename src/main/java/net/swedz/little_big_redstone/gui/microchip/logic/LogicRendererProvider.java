package net.swedz.little_big_redstone.gui.microchip.logic;

import net.swedz.little_big_redstone.microchip.logic.Logic;

public interface LogicRendererProvider<L extends Logic>
{
	LogicRenderer<L> create(Context context);
	
	record Context()
	{
	}
}
