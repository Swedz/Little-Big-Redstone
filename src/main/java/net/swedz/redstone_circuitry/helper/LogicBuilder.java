package net.swedz.redstone_circuitry.helper;

import static net.swedz.redstone_circuitry.helper.LogicHelper.*;

public final class LogicBuilder
{
	private int     output;
	private boolean desiredOutput = true;
	
	public LogicBuilder add(boolean value)
	{
		output += toInt(value);
		return this;
	}
	
	public LogicBuilder multiply(boolean value)
	{
		output *= toInt(value);
		return this;
	}
	
	public LogicBuilder invert()
	{
		desiredOutput = !desiredOutput;
		return this;
	}
	
	public boolean output()
	{
		return desiredOutput == toBoolean(output);
	}
}
