package net.swedz.little_big_redstone.microchip.object.logic.config;

import net.minecraft.network.chat.Component;

public interface LogicConfigButtonReference<T>
{
	void setText(Component text);
	
	void setTooltip(Component tooltip);
	
	T getValue();
	
	void setValue(T value);
	
	boolean isActive();
	
	void setActive(boolean active);
}
