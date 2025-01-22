package net.swedz.little_big_redstone.microchip.logic;

import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class LogicConfig<C extends LogicConfig<C>>
{
	public abstract void appendHoverText(List<Component> lines);
	
	public abstract void buildMenu(int leftPos, int topPos, LogicConfigMenuBuilder builder);
	
	public abstract C copy();
	
	public abstract int hashCode();
	
	public abstract boolean equals(Object o);
}
