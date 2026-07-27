package net.swedz.little_big_redstone.microchip.object.logic.config.menu;

import net.minecraft.network.chat.Component;

public interface LogicConfigTextLabelReference
{
	LogicConfigTextLabelReference setText(Component text);
	
	boolean isVisible();
	
	LogicConfigTextLabelReference setVisible(boolean visible);
}
