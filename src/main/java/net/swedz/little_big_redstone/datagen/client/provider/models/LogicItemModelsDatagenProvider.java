package net.swedz.little_big_redstone.datagen.client.provider.models;

import com.google.common.collect.Sets;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.client.model.logic.LogicItemModel;
import net.swedz.little_big_redstone.client.model.logic.LogicModelColorPalette;
import net.swedz.little_big_redstone.client.model.logic.TextureMap;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.calculator.LogicCalculatorMode;
import net.swedz.tesseract.neoforge.model.ModelGenerators;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

final class LogicItemModelsDatagenProvider
{
	private static final Set<LogicType> GENERATED = Sets.newHashSet();
	
	private static void registerLogicModels(ModelGenerators generators)
	{
		logicComponent(generators, LBRLogicTypes.DEBUGGER, BackgroundType.SQUARE, true);
		
		logicComponent(
				generators, LBRLogicTypes.IO, BackgroundType.CIRCLE, false, (b) -> b
						.put("input", LBR.id("logic/io_input"))
						.put("output", LBR.id("logic/io_output"))
		);
		logicComponent(generators, LBRLogicTypes.READER, BackgroundType.CIRCLE, true);
		logicComponent(generators, LBRLogicTypes.TAG, BackgroundType.CIRCLE, true);
		
		logicComponent(generators, LBRLogicTypes.NOT, BackgroundType.SQUARE, true);
		logicComponent(generators, LBRLogicTypes.AND, BackgroundType.SQUARE, true);
		logicComponent(generators, LBRLogicTypes.NAND, BackgroundType.SQUARE, true);
		logicComponent(generators, LBRLogicTypes.OR, BackgroundType.SQUARE, true);
		logicComponent(generators, LBRLogicTypes.NOR, BackgroundType.SQUARE, true);
		logicComponent(generators, LBRLogicTypes.XOR, BackgroundType.SQUARE, true);
		
		logicComponent(
				generators, LBRLogicTypes.SEQUENCER, BackgroundType.SQUARE, false, (b) -> b
						.put("progress", LBR.id("logic/sequencer"))
		);
		logicComponent(generators, LBRLogicTypes.PULSE_THROTTLER, BackgroundType.SQUARE, true);
		logicComponent(generators, LBRLogicTypes.SELECTOR, BackgroundType.SQUARE, true);
		logicComponent(generators, LBRLogicTypes.RANDOMIZER, BackgroundType.SQUARE, true);
		logicComponent(generators, LBRLogicTypes.COMPARATOR, BackgroundType.SQUARE, true);
		logicComponent(
				generators, LBRLogicTypes.CALCULATOR, BackgroundType.SQUARE, false, (b) -> b
						.put(LogicCalculatorMode.ADDITION.textureKey(), LBR.id("logic/calculator_addition"))
						.put(LogicCalculatorMode.SUBTRACTION.textureKey(), LBR.id("logic/calculator_subtraction"))
		);
		
		logicComponent(
				generators, LBRLogicTypes.T_FLIP_FLOP, BackgroundType.SQUARE, false, (b) -> b
						.put("on", LBR.id("logic/t_flip_flop_on"))
						.put("off", LBR.id("logic/t_flip_flop_off"))
		);
		logicComponent(
				generators, LBRLogicTypes.RS_NOR_LATCH, BackgroundType.SQUARE, false, (b) -> b
						.put("on", LBR.id("logic/rs_nor_latch_on"))
						.put("off", LBR.id("logic/rs_nor_latch_off"))
		);
	}
	
	static void registerModels(ModelGenerators generators)
	{
		registerLogicModels(generators);
		assertAllTypesAreGenerated();
	}
	
	private static void assertAllTypesAreGenerated()
	{
		boolean missing = false;
		for(var type : LBRLogicTypes.values())
		{
			if(!GENERATED.contains(type))
			{
				missing = true;
				LBR.LOGGER.error("Did not generate model for logic type {}, did you forget?", type.id());
			}
		}
		if(missing)
		{
			throw new IllegalStateException("Missing generated models for some logic types");
		}
	}
	
	private enum BackgroundType
	{
		SQUARE,
		CIRCLE;
		
		private final String key = this.toString().toLowerCase(Locale.ROOT);
		
		public String key()
		{
			return key;
		}
		
		public Identifier itemBackground()
		{
			return LBR.id("item/logic/background_%s".formatted(key));
		}
		
		public Identifier itemBorder()
		{
			return LBR.id("item/logic/border_%s".formatted(key));
		}
		
		public Identifier boardBackground()
		{
			return LBR.id("logic/background_%s".formatted(key));
		}
		
		public Identifier boardBorder()
		{
			return LBR.id("logic/border_%s".formatted(key));
		}
	}
	
	private static void logicComponent(
			ModelGenerators generators,
			Supplier<LogicType> entry,
			BackgroundType backgroundType,
			boolean icon,
			Function<TextureMap, TextureMap> extraBoardTextures
	)
	{
		var type = entry.get();
		GENERATED.add(type);
		var id = type.id().getPath();
		
		var colorPalette = LogicModelColorPalette.builder();
		for(var color : DyeColor.values())
		{
			colorPalette.foregroundColor(color, LBRColors.componentForeground(color));
			colorPalette.backgroundColor(color, LBRColors.componentBackground(color));
		}
		var itemTextures = TextureMap.EMPTY
				.put("background", backgroundType.itemBackground())
				.put("border", backgroundType.itemBorder())
				.put("icon", LBR.id("item/logic/%s".formatted(id)));
		var boardTextures = TextureMap.EMPTY
				.put("background", backgroundType.boardBackground())
				.put("border", backgroundType.boardBorder());
		if(icon)
		{
			boardTextures = boardTextures.put("icon", LBR.id("logic/%s".formatted(id)));
		}
		if(extraBoardTextures != null)
		{
			boardTextures = extraBoardTextures.apply(boardTextures);
		}
		var model = new LogicItemModel.Unbaked(
				Optional.empty(),
				colorPalette.build(),
				itemTextures,
				boardTextures
		);
		
		generators.item().itemModelOutput.accept(type.item(), model);
	}
	
	private static void logicComponent(ModelGenerators generators, Supplier<LogicType> entry, BackgroundType backgroundType, boolean icon)
	{
		logicComponent(generators, entry, backgroundType, icon, null);
	}
}
