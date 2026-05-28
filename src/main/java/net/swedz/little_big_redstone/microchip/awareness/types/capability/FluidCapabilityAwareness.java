package net.swedz.little_big_redstone.microchip.awareness.types.capability;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;

import java.util.List;

public final class FluidCapabilityAwareness extends CapabilityAwareness<FluidCapabilityAwareness, ResourceHandler<FluidResource>>
{
	public FluidCapabilityAwareness()
	{
		super();
	}
	
	@Override
	protected List<? extends BlockCapability<ResourceHandler<FluidResource>, Direction>> getCapabilities()
	{
		return List.of(Capabilities.Fluid.BLOCK);
	}
	
	@Override
	public AwarenessType<FluidCapabilityAwareness> type()
	{
		return AwarenessTypes.CAPABILITY_FLUID;
	}
}
