package net.swedz.little_big_redstone.microchip.object.logic;

import net.swedz.tesseract.neoforge.api.range.IntRange;

public interface LogicPortHolder
{
	IntRange inputPortsAllowed();
	
	int inputPorts();
	
	IntRange outputPortsAllowed();
	
	int outputPorts();
	
	default LogicGridSize size()
	{
		int ports = Math.max(this.inputPorts(), this.outputPorts());
		return new LogicGridSize(1, Math.max(1, ports / 2));
	}
}
