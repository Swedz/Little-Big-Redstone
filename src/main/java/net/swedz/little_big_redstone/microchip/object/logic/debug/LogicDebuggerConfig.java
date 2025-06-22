package net.swedz.little_big_redstone.microchip.object.logic.debug;

import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.tesseract.neoforge.api.range.IntRange;

public final class LogicDebuggerConfig extends LogicConfig<LogicDebuggerConfig>
{
	public LogicDebuggerConfig()
	{
	}
	
	@Override
	protected void internalLoadFrom(LogicDebuggerConfig other)
	{
	}
	
	@Override
	public void resetForPickup()
	{
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(0, 0);
	}
	
	@Override
	public int inputs()
	{
		return 0;
	}
	
	@Override
	public IntRange outputsAllowed()
	{
		return new IntRange(0, 0);
	}
	
	@Override
	public int outputs()
	{
		return 0;
	}
	
	@Override
	public int hashCode()
	{
		return 0;
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof LogicDebuggerConfig;
	}
}