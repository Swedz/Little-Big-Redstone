package net.swedz.little_big_redstone;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.swedz.little_big_redstone.client.entity.StickyNoteEntityRenderer;
import net.swedz.little_big_redstone.client.hud.FloppyDiskConsumeItemsGuiOverlay;
import net.swedz.little_big_redstone.client.item.LogicItemRenderer;
import net.swedz.little_big_redstone.client.item.StickyNoteItemRenderer;
import net.swedz.little_big_redstone.client.model.logic.LogicUnbakedModel;
import net.swedz.little_big_redstone.client.model.microchip.MicrochipUnbakedModel;
import net.swedz.little_big_redstone.client.model.stickynote.entity.StickyNoteEntityUnbakedModel;
import net.swedz.little_big_redstone.client.model.stickynote.item.StickyNoteItemUnbakedModel;
import net.swedz.little_big_redstone.gui.floppydisk.FloppyDiskScreen;
import net.swedz.little_big_redstone.gui.logicarray.LogicArrayScreen;
import net.swedz.little_big_redstone.gui.logicconfig.LogicConfigScreen;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.item.LogicItem;
import net.swedz.little_big_redstone.item.logicarray.tooltip.LogicArrayClientTooltip;
import net.swedz.little_big_redstone.item.logicarray.tooltip.LogicArrayTooltipData;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.function.Supplier;

@Mod(value = LBR.ID, dist = Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = LBR.ID, bus = EventBusSubscriber.Bus.MOD)
public final class LBRClient
{
	public LBRClient(IEventBus bus, ModContainer container)
	{
		FloppyDiskScreen.createPath();
		LBRTooltips.init();
		LogicRenderers.init();
	}
	
	private static void registerCustomItemRenderer(RegisterClientExtensionsEvent event, Supplier<BlockEntityWithoutLevelRenderer> renderer, Class<?> itemType)
	{
		event.registerItem(
				new IClientItemExtensions()
				{
					@Override
					public BlockEntityWithoutLevelRenderer getCustomRenderer()
					{
						return renderer.get();
					}
				},
				LBRItems.values().stream().filter((i) -> i.get().getClass() == itemType).map(ItemHolder::get).toArray(Item[]::new)
		);
	}
	
	@SubscribeEvent
	private static void registerClientExtensions(RegisterClientExtensionsEvent event)
	{
		registerCustomItemRenderer(event, LogicItemRenderer::new, LogicItem.class);
		registerCustomItemRenderer(event, StickyNoteItemRenderer::new, StickyNoteItem.class);
	}
	
	@SubscribeEvent
	private static void register(RegisterGuiLayersEvent event)
	{
		event.registerAbove(VanillaGuiLayers.OVERLAY_MESSAGE, LBR.id("floppy_disk_consume_items"), FloppyDiskConsumeItemsGuiOverlay::render);
	}
	
	@SubscribeEvent
	private static void registerItemProperties(FMLClientSetupEvent event)
	{
		event.enqueueWork(() -> LBRItems.values().forEach(ItemHolder::triggerClientRegistrationListener));
	}
	
	@SubscribeEvent
	private static void registerClientTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event)
	{
		event.register(LogicArrayTooltipData.class, (data) -> new LogicArrayClientTooltip(data.storage()));
	}
	
	@SubscribeEvent
	private static void registerScreens(RegisterMenuScreensEvent event)
	{
		event.register(LBRMenus.MICROCHIP.get(), MicrochipScreen::new);
		event.register(LBRMenus.LOGIC_CONFIG.get(), LogicConfigScreen::new);
		event.register(LBRMenus.LOGIC_ARRAY.get(), LogicArrayScreen::new);
	}
	
	@SubscribeEvent
	private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerEntityRenderer(LBREntities.STICKY_NOTE.get(), StickyNoteEntityRenderer::new);
	}
	
	@SubscribeEvent
	private static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event)
	{
		event.register(LogicUnbakedModel.ID, LogicUnbakedModel.LOADER);
		event.register(MicrochipUnbakedModel.ID, MicrochipUnbakedModel.LOADER);
		event.register(StickyNoteEntityUnbakedModel.ID, StickyNoteEntityUnbakedModel.LOADER);
		event.register(StickyNoteItemUnbakedModel.ID, StickyNoteItemUnbakedModel.LOADER);
	}
}
