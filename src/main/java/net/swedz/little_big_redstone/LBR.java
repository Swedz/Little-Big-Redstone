package net.swedz.little_big_redstone;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.swedz.little_big_redstone.datagen.client.provider.LanguageDatagenProvider;
import net.swedz.little_big_redstone.guide.LBRGuide;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicMode;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicComparisonMode;
import net.swedz.little_big_redstone.microchip.object.logic.io.LogicPowerOutputType;
import net.swedz.little_big_redstone.microchip.object.logic.reader.LogicReaderThreshold;
import net.swedz.little_big_redstone.network.LBRPackets;
import net.swedz.tesseract.neoforge.api.Assert;
import net.swedz.tesseract.neoforge.capabilities.CapabilitiesListeners;
import net.swedz.tesseract.neoforge.lang.LangManager;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;
import net.swedz.tesseract.neoforge.tooltip.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(LBR.ID)
public final class LBR
{
	public static final String ID   = "little_big_redstone";
	public static final String NAME = "Little Big Redstone";
	
	public static ResourceLocation id(String path)
	{
		return ResourceLocation.fromNamespaceAndPath(ID, path);
	}
	
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
	
	public LBR(IEventBus bus, ModContainer container)
	{
		setupText();
		
		LogicTypes.init();
		AwarenessTypes.init();
		
		LBRComponents.init(bus);
		LBRItems.init(bus);
		LBRBlocks.init(bus);
		LBREntities.init(bus);
		LBRCreativeTabs.init(bus);
		LBRMenus.init(bus);
		LBRRecipeTypes.init(bus);
		LBRAttachments.init(bus);
		LBRGuide.init();
		
		bus.addListener(RegisterPayloadHandlersEvent.class, LBRPackets::init);
		
		bus.addListener(
				FMLCommonSetupEvent.class,
				(event) -> event.enqueueWork(() ->
				{
					LBRItems.values().forEach(ItemHolder::triggerRegistrationListener);
					LBRBlocks.values().forEach(BlockHolder::triggerRegistrationListener);
				})
		);
		
		bus.addListener(RegisterCapabilitiesEvent.class, (event) -> CapabilitiesListeners.triggerAll(ID, event));
	}
	
	private static LBRText TEXT;
	
	public static LBRText text()
	{
		Assert.notNull(TEXT, "Text not yet loaded");
		return TEXT;
	}
	
	private static void setupText()
	{
		var instance = new LangManager(ID)
				.builtinColorStyles()
				.style("tooltip", () -> Style.EMPTY.withColor(TextColor.fromRgb(0xA9A9A9)).withItalic(false))
				.style("tooltip.header", () -> Style.EMPTY.withUnderlined(true))
				.style("highlighted", () -> Style.EMPTY.withColor(TextColor.fromRgb(0xFFDE7D)).withItalic(false))
				.style("yes", () -> Style.EMPTY.withColor(TextColor.fromRgb(0x7FFF7D)).withItalic(false))
				.style("no", () -> Style.EMPTY.withColor(TextColor.fromRgb(0xFF7D7F)).withItalic(false))
				.style("direction.down", () -> Style.EMPTY.withColor(TextColor.fromRgb(0xFFD800)).withItalic(false))
				.style("direction.up", () -> Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(false))
				.style("direction.north", () -> Style.EMPTY.withColor(TextColor.fromRgb(0x4CFF00)).withItalic(false))
				.style("direction.south", () -> Style.EMPTY.withColor(TextColor.fromRgb(0x0094FF)).withItalic(false))
				.style("direction.west", () -> Style.EMPTY.withColor(TextColor.fromRgb(0xFF6A00)).withItalic(false))
				.style("direction.east", () -> Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000)).withItalic(false))
				.style("input", () -> Style.EMPTY.withColor(TextColor.fromRgb(0x7D9EFF)).withItalic(false))
				.style("output", () -> Style.EMPTY.withColor(TextColor.fromRgb(0xFF9D7D)).withItalic(false))
				.style("complexity.low", () -> Style.EMPTY.withColor(TextColor.fromRgb(0x7FFF7D)).withItalic(false))
				.style("complexity.moderate", () -> Style.EMPTY.withColor(TextColor.fromRgb(0xFFDE7D)).withItalic(false))
				.style("complexity.high", () -> Style.EMPTY.withColor(TextColor.fromRgb(0xFFA67D)).withItalic(false))
				.style("complexity.very_high", () -> Style.EMPTY.withColor(TextColor.fromRgb(0xFF7D7F)).withItalic(false))
				
				.builtinParsers()
				.parser("percentage", float.class, () -> (value) -> Parser.FLOAT_PERCENTAGE.parse(value, 0))
				.parser("ticks_and_seconds", long.class, () -> (ticks) -> ticks != 1 ? TEXT.logicConfigButtonLabelTicksAndSeconds(ticks, ticks / 20f) : TEXT.logicConfigButtonLabelTicksAndSecondsSingular(ticks, ticks / 20f))
				.parser("yes_no", boolean.class, () -> LBRTooltips.BOOLEAN_YES_NO_PARSER)
				.parser(Direction.class, () -> LBRTooltips.DIRECTION_PARSER)
				.parser(LogicMode.class, () -> LogicMode::label)
				.parser(LogicPowerOutputType.class, () -> LogicPowerOutputType::label)
				.parser(LogicComparisonMode.class, () -> LogicComparisonMode::symbol)
				.parser(LogicReaderThreshold.class, () -> (threshold) -> threshold.isPercentage() ? Parser.FLOAT_PERCENTAGE.parse(threshold.percentage(), 2) : Component.literal(String.format("%,d", threshold.number())))
				
				.build(LBRText.class)
				.load();
		LanguageDatagenProvider.include(instance);
		TEXT = instance.lang();
	}
}
