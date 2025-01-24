package net.swedz.little_big_redstone.microchip.awareness.types.capability;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;

public final class EnergyCapabilityAwareness extends CapabilityAwareness<EnergyCapabilityAwareness, IEnergyStorage>
{
	public EnergyCapabilityAwareness()
	{
		super(Capabilities.EnergyStorage.BLOCK);
	}
	
	@Override
	public AwarenessType<EnergyCapabilityAwareness> type()
	{
		return AwarenessTypes.CAPABILITY_ENERGY;
	}
}
