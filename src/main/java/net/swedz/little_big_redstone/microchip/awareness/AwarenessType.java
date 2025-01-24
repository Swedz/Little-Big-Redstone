package net.swedz.little_big_redstone.microchip.awareness;

public record AwarenessType<A extends MicrochipAwareness>(
		String id, AwarenessFactory<A> factory
) implements AwarenessFactory<A>
{
	@Override
	public A create()
	{
		return factory.create();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof AwarenessType other && id.equals(other.id);
	}
	
	@Override
	public int hashCode()
	{
		return id.hashCode();
	}
}
