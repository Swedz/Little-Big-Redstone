package net.swedz.little_big_redstone.microchip.awareness;

import net.swedz.little_big_redstone.microchip.awareness.types.RedstoneAwareness;
import net.swedz.little_big_redstone.microchip.awareness.types.capability.EnergyCapabilityAwareness;
import net.swedz.little_big_redstone.microchip.awareness.types.capability.FluidCapabilityAwareness;
import net.swedz.little_big_redstone.microchip.awareness.types.capability.ItemCapabilityAwareness;

public final class AwarenessTypes
{
	public static final AwarenessType<ItemCapabilityAwareness>   CAPABILITY_ITEM   = register("capability_item", ItemCapabilityAwareness::new);
	public static final AwarenessType<FluidCapabilityAwareness>  CAPABILITY_FLUID  = register("capability_fluid", FluidCapabilityAwareness::new);
	public static final AwarenessType<EnergyCapabilityAwareness> CAPABILITY_ENERGY = register("capability_energy", EnergyCapabilityAwareness::new);
	
	public static final AwarenessType<RedstoneAwareness> REDSTONE = register("redstone", RedstoneAwareness::new);
	
	private static <A extends MicrochipAwareness> AwarenessType<A> register(String id, AwarenessFactory<A> factory)
	{
		return new AwarenessType(id, factory);
	}
	
	public static void init()
	{
	}
}
