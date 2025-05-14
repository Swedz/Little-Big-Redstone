package net.swedz.little_big_redstone.microchip.object;

public interface MicrochipObjectFactory<T extends MicrochipObject>
{
	T create(int slot, int x, int y);
}
