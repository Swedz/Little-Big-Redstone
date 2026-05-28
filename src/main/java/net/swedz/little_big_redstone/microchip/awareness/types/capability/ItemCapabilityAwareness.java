package net.swedz.little_big_redstone.microchip.awareness.types.capability;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;

import java.util.List;

public final class ItemCapabilityAwareness extends CapabilityAwareness<ItemCapabilityAwareness, ResourceHandler<ItemResource>>
{
	public ItemCapabilityAwareness()
	{
		super();
	}
	
	@Override
	protected List<? extends BlockCapability<ResourceHandler<ItemResource>, Direction>> getCapabilities()
	{
		return List.of(Capabilities.Item.BLOCK);
	}
	
	@Override
	public AwarenessType<ItemCapabilityAwareness> type()
	{
		return AwarenessTypes.CAPABILITY_ITEM;
	}
}
