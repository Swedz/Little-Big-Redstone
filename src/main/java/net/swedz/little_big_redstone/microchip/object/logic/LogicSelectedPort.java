package net.swedz.little_big_redstone.microchip.object.logic;

import net.swedz.little_big_redstone.microchip.wire.PortReference;

public record LogicSelectedPort(LogicEntry entry, int index) implements PortReference
{
	@Override
	public int slot()
	{
		return entry.slot();
	}
}
