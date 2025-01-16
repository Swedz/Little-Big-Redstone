package net.swedz.redstone_circuitry.item.gate;

import net.swedz.redstone_circuitry.helper.LogicBuilder;

public final class NORGate implements LogicGate
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
			logic.add(input);
		}
		return logic.invert().output();
	}
}
