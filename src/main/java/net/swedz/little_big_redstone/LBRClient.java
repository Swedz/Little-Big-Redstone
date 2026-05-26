package net.swedz.little_big_redstone;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import net.swedz.little_big_redstone.client.entity.StickyNoteEntityRenderer;
import net.swedz.little_big_redstone.client.hud.FloppyDiskConsumeItemsGuiOverlay;
import net.swedz.little_big_redstone.client.hud.NoteBoardGuiOverlay;
import net.swedz.little_big_redstone.client.hud.StickyNoteViewContentsGuiOverlay;
import net.swedz.little_big_redstone.client.item.LogicItemRenderer;
import net.swedz.little_big_redstone.client.item.StickyNoteInHandItemRenderer;
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
import net.swedz.little_big_redstone.gui.microchip.wire.WirePathing;
import net.swedz.little_big_redstone.gui.noteboard.NoteBoardScreen;
import net.swedz.little_big_redstone.item.LogicItem;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.item.stickynote.tooltip.StickyNoteClientTooltip;
import net.swedz.little_big_redstone.item.stickynote.tooltip.StickyNoteTooltipData;
import net.swedz.little_big_redstone.item.tooltip.ItemContainerContentsClientTooltip;
import net.swedz.little_big_redstone.item.tooltip.ItemContainerContentsTooltipData;
import net.swedz.tesseract.api.Assert;
import net.swedz.tesseract.config.ConfigManager;
import net.swedz.tesseract.neoforge.config.ModConfigFileAccess;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.function.Supplier;

@Mod(value = LBR.ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = LBR.ID, value = Dist.CLIENT)
public final class LBRClient
{
	public LBRClient(IEventBus bus, ModContainer container)
	{
		setupConfig(bus, container);
		
		LBRKeybinds.init(bus);
		FloppyDiskScreen.createPath();
		LBRTooltips.init();
		LogicRenderers.init();
		
		NeoForge.EVENT_BUS.addListener(GameShuttingDownEvent.class, (event) -> WirePathing.shutdownExecutor());
	}
	
	private static LBRClientConfig CONFIG;
	
	public static LBRClientConfig config()
	{
		Assert.notNull(CONFIG, "Config not yet loaded");
		return CONFIG;
	}
	
	private static void setupConfig(IEventBus bus, ModContainer container)
	{
		var file = new ModConfigFileAccess(container, ModConfig.Type.CLIENT);
		var instance = new ConfigManager(file)
				.build(LBRClientConfig.class)
				.load();
		bus.addListener(FMLCommonSetupEvent.class, (event) -> instance.load(false));
		CONFIG = instance.config();
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
	private static void renderHand(RenderHandEvent event)
	{
		if(event.getItemStack().getItem() instanceof StickyNoteItem)
		{
			StickyNoteInHandItemRenderer.renderItemFirstPerson(
					event.getPoseStack(),
					event.getSubmitNodeCollector(),
					event.getPackedLight(),
					event.getHand(),
					event.getInterpolatedPitch(),
					event.getEquipProgress(),
					event.getSwingProgress(),
					event.getItemStack()
			);
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	private static void register(RegisterGuiLayersEvent event)
	{
		event.registerAbove(VanillaGuiLayers.HOTBAR, LBR.id("floppy_disk_consume_items"), FloppyDiskConsumeItemsGuiOverlay::render);
		event.registerBelow(VanillaGuiLayers.HOTBAR, LBR.id("sticky_note_view_contents"), StickyNoteViewContentsGuiOverlay::extract);
		event.registerBelow(LBR.id("sticky_note_view_contents"), LBR.id("note_board"), NoteBoardGuiOverlay::extract);
	}
	
	@SubscribeEvent
	private static void registerItemProperties(FMLClientSetupEvent event)
	{
		event.enqueueWork(() -> LBRItems.values().forEach(ItemHolder::triggerClientRegistrationListener));
	}
	
	@SubscribeEvent
	private static void registerClientTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event)
	{
		event.register(ItemContainerContentsTooltipData.class, (data) -> new ItemContainerContentsClientTooltip(data.storage(), data.maxColumns(), data.maxRows(), data.showExtraSlot()));
		event.register(StickyNoteTooltipData.class, (data) -> new StickyNoteClientTooltip(data.note()));
	}
	
	@SubscribeEvent
	private static void registerScreens(RegisterMenuScreensEvent event)
	{
		event.register(LBRMenus.MICROCHIP.get(), MicrochipScreen::new);
		event.register(LBRMenus.LOGIC_CONFIG.get(), LogicConfigScreen::new);
		event.register(LBRMenus.LOGIC_ARRAY.get(), LogicArrayScreen::new);
		event.register(LBRMenus.NOTE_BOARD.get(), NoteBoardScreen::new);
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
