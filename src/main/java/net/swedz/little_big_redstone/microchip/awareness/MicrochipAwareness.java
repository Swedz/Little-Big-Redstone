package net.swedz.little_big_redstone.microchip.awareness;

import net.swedz.little_big_redstone.microchip.Microchip;

public abstract class MicrochipAwareness<A extends MicrochipAwareness<A>> implements AwarenessListener
{
	public abstract AwarenessType<A> type();
	
	@Override
	public void load(Microchip microchip)
	{
	}
}
