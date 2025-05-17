package net.swedz.little_big_redstone.microchip.object.logic;

import net.swedz.tesseract.neoforge.api.range.IntRange;

public interface LogicPortHolder
{
	IntRange inputsAllowed();
	
	int inputs();
	
	IntRange outputsAllowed();
	
	int outputs();
}
