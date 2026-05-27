package net.swedz.little_big_redstone.datagen.client.provider.models;

import com.google.common.collect.Sets;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.client.model.logic.LogicItemModel;
import net.swedz.little_big_redstone.client.model.logic.LogicModelColorPalette;
import net.swedz.little_big_redstone.client.model.logic.TextureMap;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.calculator.LogicCalculatorMode;
import net.swedz.tesseract.neoforge.model.ModelGenerators;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public final class LogicItemModelsDatagenProvider extends ModelProvider
{
	private final Set<LogicType<?>> generated = Sets.newHashSet();
	
	public LogicItemModelsDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), LBR.ID);
	}
	
	private void registerLogicModels(ModelGenerators generators)
	{
		this.logicComponent(generators, LogicTypes.DEBUGGER, BackgroundType.SQUARE, true);
		
		this.logicComponent(generators, LogicTypes.IO, BackgroundType.CIRCLE, false, (b) -> b
				.put("input", LBR.id("logic/io_input"))
				.put("output", LBR.id("logic/io_output")));
		this.logicComponent(generators, LogicTypes.READER, BackgroundType.CIRCLE, true);
		this.logicComponent(generators, LogicTypes.TAG, BackgroundType.CIRCLE, true);
		
		this.logicComponent(generators, LogicTypes.NOT, BackgroundType.SQUARE, true);
		this.logicComponent(generators, LogicTypes.AND, BackgroundType.SQUARE, true);
		this.logicComponent(generators, LogicTypes.NAND, BackgroundType.SQUARE, true);
		this.logicComponent(generators, LogicTypes.OR, BackgroundType.SQUARE, true);
		this.logicComponent(generators, LogicTypes.NOR, BackgroundType.SQUARE, true);
		this.logicComponent(generators, LogicTypes.XOR, BackgroundType.SQUARE, true);
		
		this.logicComponent(generators, LogicTypes.SEQUENCER, BackgroundType.SQUARE, false, (b) -> b
				.put("progress", LBR.id("logic/sequencer")));
		this.logicComponent(generators, LogicTypes.PULSE_THROTTLER, BackgroundType.SQUARE, true);
		this.logicComponent(generators, LogicTypes.SELECTOR, BackgroundType.SQUARE, true);
		this.logicComponent(generators, LogicTypes.RANDOMIZER, BackgroundType.SQUARE, true);
		this.logicComponent(generators, LogicTypes.COMPARATOR, BackgroundType.SQUARE, true);
		this.logicComponent(generators, LogicTypes.CALCULATOR, BackgroundType.SQUARE, false, (b) -> b
				.put(LogicCalculatorMode.ADDITION.textureKey(), LBR.id("logic/calculator_addition"))
				.put(LogicCalculatorMode.SUBTRACTION.textureKey(), LBR.id("logic/calculator_subtraction")));
		
		this.logicComponent(generators, LogicTypes.T_FLIP_FLOP, BackgroundType.SQUARE, false, (b) -> b
				.put("on", LBR.id("logic/t_flip_flop_on"))
				.put("off", LBR.id("logic/t_flip_flop_off")));
		this.logicComponent(generators, LogicTypes.RS_NOR_LATCH, BackgroundType.SQUARE, false, (b) -> b
				.put("on", LBR.id("logic/rs_nor_latch_on"))
				.put("off", LBR.id("logic/rs_nor_latch_off")));
	}
	
	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels)
	{
		var generators = new ModelGenerators(blockModels, itemModels);
		this.registerLogicModels(generators);
		this.assertAllTypesAreGenerated();
	}
	
	private void assertAllTypesAreGenerated()
	{
		boolean missing = false;
		for(var type : LogicTypes.values())
		{
			if(!generated.contains(type))
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
	
	private void logicComponent(
			ModelGenerators generators,
			LogicType<?> type,
			BackgroundType backgroundType,
			boolean icon,
			Function<TextureMap, TextureMap> extraBoardTextures
	)
	{
		generated.add(type);
		String id = type.id();
		
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
	
	private void logicComponent(ModelGenerators generators, LogicType<?> type, BackgroundType backgroundType, boolean icon)
	{
		this.logicComponent(generators, type, backgroundType, icon, null);
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
