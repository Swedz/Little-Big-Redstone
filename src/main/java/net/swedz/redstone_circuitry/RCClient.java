package net.swedz.redstone_circuitry;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.swedz.redstone_circuitry.gui.microchip.MicrochipScreen;

@Mod(value = RedstoneCircuitry.ID, dist = Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = RedstoneCircuitry.ID, bus = EventBusSubscriber.Bus.MOD)
public final class RCClient
{
	public RCClient(IEventBus bus, ModContainer container)
	{
		RCTooltips.init();
	}
	
	@SubscribeEvent
	private static void registerScreens(RegisterMenuScreensEvent event)
	{
		event.register(RCMenus.MICROCHIP.get(), MicrochipScreen::new);
	}
}
