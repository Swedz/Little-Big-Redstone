package net.swedz.little_big_redstone.microchip.awareness.types.capability;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;

import java.util.List;

public final class EnergyCapabilityAwareness extends CapabilityAwareness<EnergyCapabilityAwareness, EnergyHandler>
{
	public EnergyCapabilityAwareness()
	{
		super();
	}
	
	@Override
	protected List<? extends BlockCapability<EnergyHandler, Direction>> getCapabilities()
	{
		return BlockCapability.getAll().stream()
				.filter((capability) ->
						EnergyHandler.class.isAssignableFrom(capability.typeClass()) &&
						capability.contextClass() == Direction.class)
				.map((capability) -> (BlockCapability<EnergyHandler, Direction>) capability)
				.toList();
	}
	
	@Override
	public AwarenessType<EnergyCapabilityAwareness> type()
	{
		return AwarenessTypes.CAPABILITY_ENERGY;
	}
}
