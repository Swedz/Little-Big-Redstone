package net.swedz.little_big_redstone.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.datagen.client.DatagenDelegatorClient;
import net.swedz.little_big_redstone.datagen.server.DatagenDelegatorServer;

@EventBusSubscriber(modid = LBR.ID)
public final class DatagenDelegator
{
	@SubscribeEvent
	private static void gatherData(GatherDataEvent.Client event)
	{
		DatagenDelegatorClient.configure(event);
		DatagenDelegatorServer.configure(event);
	}
}
