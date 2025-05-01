package net.swedz.little_big_redstone.microchip.logic.config;

import net.minecraft.network.chat.Component;

public interface LogicConfigButtonReference<T>
{
	void setText(Component text);
	
	void setTooltip(Component tooltip);
	
	void setValue(T value);
}
