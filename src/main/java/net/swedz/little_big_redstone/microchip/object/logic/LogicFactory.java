package net.swedz.little_big_redstone.microchip.object.logic;

public interface LogicFactory<L extends LogicComponent>
{
	L create();
}
