package net.swedz.little_big_redstone.microchip.awareness.types.capability;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;

import java.util.List;

public final class ItemCapabilityAwareness extends CapabilityAwareness<ItemCapabilityAwareness, IItemHandler>
{
	public ItemCapabilityAwareness()
	{
		super();
	}
	
	@Override
	protected List<? extends BlockCapability<IItemHandler, Direction>> getCapabilities()
	{
		return List.of(Capabilities.ItemHandler.BLOCK);
	}
	
	@Override
	public AwarenessType<ItemCapabilityAwareness> type()
	{
		return AwarenessTypes.CAPABILITY_ITEM;
	}
}
