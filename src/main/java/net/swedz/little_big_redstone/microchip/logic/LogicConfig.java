package net.swedz.little_big_redstone.microchip.logic;

public abstract class LogicConfig<C extends LogicConfig<C>>
{
	public abstract C copy();
	
	public abstract int hashCode();
	
	public abstract boolean equals(Object o);
}
