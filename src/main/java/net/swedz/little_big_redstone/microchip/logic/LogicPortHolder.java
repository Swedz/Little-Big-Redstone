package net.swedz.little_big_redstone.microchip.logic;

import net.swedz.little_big_redstone.api.IntRange;

public interface LogicPortHolder
{
	IntRange inputsAllowed();
	
	int inputs();
	
	IntRange outputsAllowed();
	
	int outputs();
}
