package net.swedz.little_big_redstone.gui.microchip.logic;

import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;

public interface LogicRendererProvider<L extends LogicComponent>
{
	LogicRenderer<L> create(Context context);
	
	record Context()
	{
	}
}
