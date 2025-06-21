package net.swedz.little_big_redstone.microchip.object.logic.latch.rs;

import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.tesseract.neoforge.api.range.IntRange;

public final class RSNORLatchConfig extends LogicConfig<RSNORLatchConfig>
{
	public RSNORLatchConfig()
	{
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(2, 2);
	}
	
	@Override
	public int inputs()
	{
		return 2;
	}
	
	@Override
	public IntRange outputsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public int outputs()
	{
		return 1;
	}
	
	@Override
	protected void internalLoadFrom(RSNORLatchConfig other)
	{
	}
	
	@Override
	public void resetForPickup()
	{
	}
	
	@Override
	public int hashCode()
	{
		return 0;
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof RSNORLatchConfig;
	}
}
