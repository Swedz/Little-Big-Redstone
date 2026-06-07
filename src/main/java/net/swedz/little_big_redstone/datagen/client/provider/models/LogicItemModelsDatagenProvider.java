package net.swedz.little_big_redstone.datagen.client.provider.models;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.client.model.logic.LogicBakingModelData;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.calculator.LogicCalculatorMode;

import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class LogicItemModelsDatagenProvider extends ItemModelProvider
{
	private final Set<LogicType> generated = Sets.newHashSet();
	
	public LogicItemModelsDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), LBR.ID, event.getExistingFileHelper());
	}
	
	private void registerLogicModels()
	{
		this.logicComponent(LBRLogicTypes.DEBUGGER, BackgroundType.SQUARE, true);
		
		this.logicComponent(
				LBRLogicTypes.IO, BackgroundType.CIRCLE, false,
				(b) -> b
						.boardTexture("input", LBR.id("logic/io_input"))
						.boardTexture("output", LBR.id("logic/io_output"))
		);
		this.logicComponent(LBRLogicTypes.READER, BackgroundType.CIRCLE, true);
		this.logicComponent(LBRLogicTypes.TAG, BackgroundType.CIRCLE, true);
		
		this.logicComponent(LBRLogicTypes.NOT, BackgroundType.SQUARE, true);
		this.logicComponent(LBRLogicTypes.AND, BackgroundType.SQUARE, true);
		this.logicComponent(LBRLogicTypes.NAND, BackgroundType.SQUARE, true);
		this.logicComponent(LBRLogicTypes.OR, BackgroundType.SQUARE, true);
		this.logicComponent(LBRLogicTypes.NOR, BackgroundType.SQUARE, true);
		this.logicComponent(LBRLogicTypes.XOR, BackgroundType.SQUARE, true);
		
		this.logicComponent(
				LBRLogicTypes.SEQUENCER, BackgroundType.SQUARE, false,
				(b) -> b
						.boardTexture("progress", LBR.id("logic/sequencer"))
		);
		this.logicComponent(LBRLogicTypes.PULSE_THROTTLER, BackgroundType.SQUARE, true);
		this.logicComponent(LBRLogicTypes.SELECTOR, BackgroundType.SQUARE, true);
		this.logicComponent(LBRLogicTypes.RANDOMIZER, BackgroundType.SQUARE, true);
		this.logicComponent(LBRLogicTypes.COMPARATOR, BackgroundType.SQUARE, true);
		this.logicComponent(
				LBRLogicTypes.CALCULATOR, BackgroundType.SQUARE, false,
				(b) -> b
						.boardTexture(LogicCalculatorMode.ADDITION.textureKey(), LBR.id("logic/calculator_addition"))
						.boardTexture(LogicCalculatorMode.SUBTRACTION.textureKey(), LBR.id("logic/calculator_subtraction"))
		);
		
		this.logicComponent(
				LBRLogicTypes.T_FLIP_FLOP, BackgroundType.SQUARE, false,
				(b) -> b
						.boardTexture("on", LBR.id("logic/t_flip_flop_on"))
						.boardTexture("off", LBR.id("logic/t_flip_flop_off"))
		);
		this.logicComponent(
				LBRLogicTypes.RS_NOR_LATCH, BackgroundType.SQUARE, false,
				(b) -> b
						.boardTexture("on", LBR.id("logic/rs_nor_latch_on"))
						.boardTexture("off", LBR.id("logic/rs_nor_latch_off"))
		);
	}
	
	@Override
	protected void registerModels()
	{
		this.registerLogicModels();
		this.assertAllTypesAreGenerated();
	}
	
	private void assertAllTypesAreGenerated()
	{
		boolean missing = false;
		for(var type : LBRLogicTypes.values())
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
		
		public ResourceLocation itemBackground()
		{
			return LBR.id("item/logic/background_%s".formatted(key));
		}
		
		public ResourceLocation itemBorder()
		{
			return LBR.id("item/logic/border_%s".formatted(key));
		}
		
		public ResourceLocation boardBackground()
		{
			return LBR.id("logic/background_%s".formatted(key));
		}
		
		public ResourceLocation boardBorder()
		{
			return LBR.id("logic/border_%s".formatted(key));
		}
	}
	
	private void logicComponent(
			Supplier<LogicType> entry,
			BackgroundType backgroundType,
			boolean icon,
			Consumer<LogicBakingModelData.Builder<?>> also
	)
	{
		var type = entry.get();
		generated.add(type);
		var name = type.id().getPath();
		this.getBuilder("item/%s".formatted(name))
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.customLoader((parent, efh) ->
				{
					var builder = LogicBakingModelData.builder(parent, efh)
							.itemTexture("background", backgroundType.itemBackground())
							.itemTexture("border", backgroundType.itemBorder())
							.itemTexture("icon", LBR.id("item/logic/%s".formatted(name)))
							.boardTexture("background", backgroundType.boardBackground())
							.boardTexture("border", backgroundType.boardBorder());
					if(icon)
					{
						builder.boardTexture("icon", LBR.id("logic/%s".formatted(name)));
					}
					for(var color : DyeColor.values())
					{
						builder.foregroundColor(color, LBRColors.componentForeground(color));
						builder.backgroundColor(color, LBRColors.componentBackground(color));
					}
					if(also != null)
					{
						also.accept(builder);
					}
					return builder;
				})
				.end();
	}
	
	private void logicComponent(
			Supplier<LogicType> type,
			BackgroundType backgroundType,
			boolean icon
	)
	{
		this.logicComponent(type, backgroundType, icon, null);
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
