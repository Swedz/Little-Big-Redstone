package net.swedz.little_big_redstone.microchip.awareness;

public interface AwarenessFactory<A extends MicrochipAwareness>
{
	A create();
}
