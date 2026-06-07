package net.swedz.little_big_redstone.gui.microchip.logic;

import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;

public interface LogicRendererProvider<L extends LogicComponent<L, C>, C extends LogicConfig>
{
	LogicRenderer<L, C> create();
}
