package net.swedz.little_big_redstone;

import com.mojang.math.OctahedralGroup;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
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
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterItemModelsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import net.swedz.little_big_redstone.client.ber.MicrochipBlockEntityRenderer;
import net.swedz.little_big_redstone.client.entity.StickyNoteEntityRenderer;
import net.swedz.little_big_redstone.client.hud.FloppyDiskConsumeItemsGuiOverlay;
import net.swedz.little_big_redstone.client.hud.NoteBoardGuiOverlay;
import net.swedz.little_big_redstone.client.hud.StickyNoteViewContentsGuiOverlay;
import net.swedz.little_big_redstone.client.item.StickyNoteInHandItemRenderer;
import net.swedz.little_big_redstone.client.model.logic.LogicItemModel;
import net.swedz.little_big_redstone.client.model.microchip.MicrochipBlockModel;
import net.swedz.little_big_redstone.gui.floppydisk.FloppyDiskScreen;
import net.swedz.little_big_redstone.gui.logicarray.LogicArrayScreen;
import net.swedz.little_big_redstone.gui.logicconfig.LogicConfigScreen;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.gui.microchip.logic.RegisterLogicRenderersEvent;
import net.swedz.little_big_redstone.gui.microchip.logic.renderer.CalculatorLogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.renderer.IORenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.renderer.OnOffLogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.renderer.SequencerRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.renderer.SimpleLogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.wire.WirePathing;
import net.swedz.little_big_redstone.gui.noteboard.NoteBoardScreen;
import net.swedz.little_big_redstone.guide.LBRGuide;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.item.stickynote.tooltip.StickyNoteClientTooltip;
import net.swedz.little_big_redstone.item.stickynote.tooltip.StickyNoteTooltipData;
import net.swedz.little_big_redstone.item.tooltip.ItemContainerContentsClientTooltip;
import net.swedz.little_big_redstone.item.tooltip.ItemContainerContentsTooltipData;
import net.swedz.tesseract.api.Assert;
import net.swedz.tesseract.config.ConfigManager;
import net.swedz.tesseract.neoforge.config.ModConfigFileAccess;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.Map;

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
		LBRGuide.init();
		
		bus.addListener(
				FMLClientSetupEvent.class,
				(event) -> event.enqueueWork(() ->
						LogicRenderers.setup(bus))
		);
		
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
	
	@SubscribeEvent
	private static void renderHand(RenderHandEvent event)
	{
		if(event.getItemStack().getItem() instanceof StickyNoteItem &&
		   StickyNoteItem.hasRelevantComponents(event.getItemStack()))
		{
			StickyNoteInHandItemRenderer.submit(
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
		event.registerAbove(VanillaGuiLayers.HOTBAR, LBR.id("floppy_disk_consume_items"), FloppyDiskConsumeItemsGuiOverlay::extract);
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
		event.registerBlockEntityRenderer(LBRBlocks.MICROCHIP_ENTITY.get(), MicrochipBlockEntityRenderer::new);
	}
	
	@SubscribeEvent
	private static void registerItemModels(RegisterItemModelsEvent event)
	{
		event.register(LBR.id("logic"), LogicItemModel.Unbaked.CODEC);
	}
	
	@SubscribeEvent
	private static void registerBlockModels(RegisterBlockStateModels event)
	{
		event.registerModel(MicrochipBlockModel.Unbaked.ID, MicrochipBlockModel.Unbaked.CODEC);
	}
	
	private static final Map<Direction, StandaloneModelKey<BlockStateModelPart>> MICROCHIP_OVERLAY_MODELS = Util.makeEnumMap(
			Direction.class,
			(direction) -> new StandaloneModelKey(() -> "Microchip Overlay Model")
	);
	
	public static StandaloneModelKey<BlockStateModelPart> getMicrochipOverlayModel(Direction direction)
	{
		Assert.notNull(direction);
		return MICROCHIP_OVERLAY_MODELS.get(direction);
	}
	
	@SubscribeEvent
	private static void registerStandaloneModels(ModelEvent.RegisterStandalone event)
	{
		for(var direction : Direction.values())
		{
			var rotation = switch(direction)
			{
				case DOWN -> OctahedralGroup.BLOCK_ROT_X_90;
				case UP -> OctahedralGroup.BLOCK_ROT_X_270;
				case NORTH -> OctahedralGroup.IDENTITY;
				case SOUTH -> OctahedralGroup.BLOCK_ROT_Y_180;
				case WEST -> OctahedralGroup.BLOCK_ROT_Y_270;
				case EAST -> OctahedralGroup.BLOCK_ROT_Y_90;
			};
			event.register(
					getMicrochipOverlayModel(direction),
					SimpleUnbakedStandaloneModel.simpleModelWrapper(
							LBR.id("block/microchip/side_overlay_" + direction.getName()),
							BlockModelRotation.get(rotation)
					)
			);
		}
	}
	
	@SubscribeEvent
	private static void registerLogicRenderers(RegisterLogicRenderersEvent event)
	{
		event.register(LBRLogicTypes.DEBUGGER, SimpleLogicRenderer::new);
		
		event.register(LBRLogicTypes.IO, IORenderer::new);
		event.register(LBRLogicTypes.READER, SimpleLogicRenderer::new);
		event.register(LBRLogicTypes.TAG, SimpleLogicRenderer::new);
		
		event.register(LBRLogicTypes.NOT, SimpleLogicRenderer::new);
		event.register(LBRLogicTypes.AND, SimpleLogicRenderer::new);
		event.register(LBRLogicTypes.NAND, SimpleLogicRenderer::new);
		event.register(LBRLogicTypes.OR, SimpleLogicRenderer::new);
		event.register(LBRLogicTypes.NOR, SimpleLogicRenderer::new);
		event.register(LBRLogicTypes.XOR, SimpleLogicRenderer::new);
		
		event.register(LBRLogicTypes.SEQUENCER, SequencerRenderer::new);
		event.register(LBRLogicTypes.PULSE_THROTTLER, SimpleLogicRenderer::new);
		event.register(LBRLogicTypes.SELECTOR, SimpleLogicRenderer::new);
		event.register(LBRLogicTypes.RANDOMIZER, SimpleLogicRenderer::new);
		event.register(LBRLogicTypes.COMPARATOR, SimpleLogicRenderer::new);
		event.register(LBRLogicTypes.CALCULATOR, CalculatorLogicRenderer::new);
		
		event.register(LBRLogicTypes.T_FLIP_FLOP, OnOffLogicRenderer::new);
		event.register(LBRLogicTypes.RS_NOR_LATCH, OnOffLogicRenderer::new);
	}
}
