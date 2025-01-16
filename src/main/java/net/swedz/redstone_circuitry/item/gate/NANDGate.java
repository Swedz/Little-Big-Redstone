package net.swedz.redstone_circuitry.item.gate;

import net.swedz.redstone_circuitry.helper.LogicBuilder;

public final class NANDGate implements LogicGate
{
	@Override
	public int inputCount()
	{
		return 3;
	}
	
	@Override
	public boolean process(boolean[] inputs)
	{
		var logic = new LogicBuilder();
		for(boolean input : inputs)
		{
			logic.multiply(input);
		}
		return logic.invert().output();
	}
}
