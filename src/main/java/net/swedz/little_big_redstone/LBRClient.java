package net.swedz.little_big_redstone;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntityRenderer;
import net.swedz.little_big_redstone.gui.logicconfig.LogicConfigScreen;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;

@Mod(value = LBR.ID, dist = Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = LBR.ID, bus = EventBusSubscriber.Bus.MOD)
public final class LBRClient
{
	public LBRClient(IEventBus bus, ModContainer container)
	{
		LBRTooltips.init();
	}
	
	@SubscribeEvent
	private static void registerScreens(RegisterMenuScreensEvent event)
	{
		event.register(LBRMenus.MICROCHIP.get(), MicrochipScreen::new);
		event.register(LBRMenus.LOGIC_CONFIG.get(), LogicConfigScreen::new);
	}
	
	@SubscribeEvent
	private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerEntityRenderer(LBREntities.STICKY_NOTE.get(), StickyNoteEntityRenderer::new);
	}
}
