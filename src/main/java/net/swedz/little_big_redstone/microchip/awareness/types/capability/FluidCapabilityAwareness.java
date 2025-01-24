package net.swedz.little_big_redstone.microchip.awareness.types.capability;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;

public final class FluidCapabilityAwareness extends CapabilityAwareness<FluidCapabilityAwareness, IFluidHandler>
{
	public FluidCapabilityAwareness()
	{
		super(Capabilities.FluidHandler.BLOCK);
	}
	
	@Override
	public AwarenessType<FluidCapabilityAwareness> type()
	{
		return AwarenessTypes.CAPABILITY_FLUID;
	}
}
