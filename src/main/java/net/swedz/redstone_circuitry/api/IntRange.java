package net.swedz.redstone_circuitry.api;

public record IntRange(int min, int max)
{
	public boolean contains(int value)
	{
		return value >= min && value <= max;
	}
}
