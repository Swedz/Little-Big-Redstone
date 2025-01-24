package net.swedz.little_big_redstone.microchip.awareness.types.capability;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;

public final class ItemCapabilityAwareness extends CapabilityAwareness<ItemCapabilityAwareness, IItemHandler>
{
	public ItemCapabilityAwareness()
	{
		super(Capabilities.ItemHandler.BLOCK);
	}
	
	@Override
	public AwarenessType<ItemCapabilityAwareness> type()
	{
		return AwarenessTypes.CAPABILITY_ITEM;
	}
}
