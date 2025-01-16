package net.swedz.redstone_circuitry.item.gate;

public final class NOTGate implements LogicGate
{
	@Override
	public int inputCount()
	{
		return 1;
	}
	
	@Override
	public boolean process(boolean[] inputs)
	{
		return !inputs[0];
	}
}
