package net.swedz.redstone_circuitry.api;

import net.swedz.tesseract.neoforge.api.Assert;

public record IntRange(int min, int max)
{
	public IntRange
	{
		Assert.that(min <= max, "Cannot create int range with min that is greater than the max (%d > %d)".formatted(min, max));
	}
	
	public boolean isSingle()
	{
		return min == max;
	}
	
	public boolean contains(int value)
	{
		return value >= min && value <= max;
	}
}
