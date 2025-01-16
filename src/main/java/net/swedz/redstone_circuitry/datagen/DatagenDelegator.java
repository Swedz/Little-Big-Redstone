package net.swedz.redstone_circuitry.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.redstone_circuitry.RedstoneCircuitry;
import net.swedz.redstone_circuitry.datagen.client.DatagenDelegatorClient;

@EventBusSubscriber(modid = RedstoneCircuitry.ID, bus = EventBusSubscriber.Bus.MOD)
public final class DatagenDelegator
{
	@SubscribeEvent
	private static void gatherData(GatherDataEvent event)
	{
		DatagenDelegatorClient.configure(event);
	}
}
