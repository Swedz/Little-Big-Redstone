package net.swedz.little_big_redstone.microchip.awareness.types.capability;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;

import java.util.List;

public final class EnergyCapabilityAwareness extends CapabilityAwareness<EnergyCapabilityAwareness, IEnergyStorage>
{
	public EnergyCapabilityAwareness()
	{
		super();
	}
	
	@Override
	protected List<? extends BlockCapability<IEnergyStorage, Direction>> getCapabilities()
	{
		return BlockCapability.getAll().stream()
				.filter((capability) ->
						IEnergyStorage.class.isAssignableFrom(capability.typeClass()) &&
						capability.contextClass() == Direction.class)
				.map((capability) -> (BlockCapability<IEnergyStorage, Direction>) capability)
				.toList();
	}
	
	@Override
	public AwarenessType<EnergyCapabilityAwareness> type()
	{
		return AwarenessTypes.CAPABILITY_ENERGY;
	}
}
