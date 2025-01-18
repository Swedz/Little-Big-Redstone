package net.swedz.little_big_redstone.microchip.logic;

public abstract class LogicConfig<C extends LogicConfig<C>>
{
	public abstract C copy();
}
