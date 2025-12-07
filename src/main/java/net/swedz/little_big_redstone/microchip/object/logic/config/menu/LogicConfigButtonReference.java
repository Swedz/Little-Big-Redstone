package net.swedz.little_big_redstone.microchip.object.logic.config.menu;

import net.minecraft.network.chat.Component;

public interface LogicConfigButtonReference<T>
{
	LogicConfigButtonReference<T> setText(Component text);
	
	LogicConfigButtonReference<T> setTooltip(Component tooltip);
	
	T getValue();
	
	LogicConfigButtonReference<T> setValue(T value);
	
	boolean isActive();
	
	LogicConfigButtonReference<T> setActive(boolean active);
	
	boolean isVisible();
	
	LogicConfigButtonReference<T> setVisible(boolean visible);
}
