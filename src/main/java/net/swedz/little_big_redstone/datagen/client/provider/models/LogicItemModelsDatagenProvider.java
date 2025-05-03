package net.swedz.little_big_redstone.datagen.client.provider.models;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.client.model.logic.LogicBakingModelData;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;

import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

public final class LogicItemModelsDatagenProvider extends ItemModelProvider
{
	private final Set<LogicType<?>> generated = Sets.newHashSet();
	
	public LogicItemModelsDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), LBR.ID, event.getExistingFileHelper());
	}
	
	private void registerLogicModels()
	{
		this.logicComponent(LogicTypes.DEBUGGER, BackgroundType.SQUARE, true);
		
		this.logicComponent(LogicTypes.IO, BackgroundType.CIRCLE, false, (b) -> b
				.boardTexture("input", LBR.id("logic/io_input"))
				.boardTexture("output", LBR.id("logic/io_output")));
		
		this.logicComponent(LogicTypes.NOT, BackgroundType.SQUARE, true);
		this.logicComponent(LogicTypes.AND, BackgroundType.SQUARE, true);
		this.logicComponent(LogicTypes.NAND, BackgroundType.SQUARE, true);
		this.logicComponent(LogicTypes.OR, BackgroundType.SQUARE, true);
		this.logicComponent(LogicTypes.NOR, BackgroundType.SQUARE, true);
		this.logicComponent(LogicTypes.XOR, BackgroundType.SQUARE, true);
		
		this.logicComponent(LogicTypes.READER, BackgroundType.CIRCLE, true);
		
		this.logicComponent(LogicTypes.SEQUENCER, BackgroundType.SQUARE, false, (b) -> b
				.boardTexture("progress", LBR.id("logic/sequencer")));
		this.logicComponent(LogicTypes.PULSE_THROTTLER, BackgroundType.SQUARE, true);
		this.logicComponent(LogicTypes.SELECTOR, BackgroundType.SQUARE, true);
		this.logicComponent(LogicTypes.RANDOMIZER, BackgroundType.SQUARE, true);
		
		this.logicComponent(LogicTypes.T_FLIP_FLOP, BackgroundType.SQUARE, false, (b) -> b
				.boardTexture("on", LBR.id("logic/t_flip_flop_on"))
				.boardTexture("off", LBR.id("logic/t_flip_flop_off")));
		this.logicComponent(LogicTypes.RS_NOR_LATCH, BackgroundType.SQUARE, false, (b) -> b
				.boardTexture("on", LBR.id("logic/rs_nor_latch_on"))
				.boardTexture("off", LBR.id("logic/rs_nor_latch_off")));
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
		
		public ResourceLocation itemBackground()
		{
			return LBR.id("item/logic_background_%s".formatted(key));
		}
		
		public ResourceLocation itemBorder()
		{
			return LBR.id("item/logic_border_%s".formatted(key));
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
	
	private void logicComponent(LogicType<?> type, BackgroundType backgroundType, boolean icon, Consumer<LogicBakingModelData.Builder<?>> also)
	{
		generated.add(type);
		String id = type.id();
		this.getBuilder("item/%s".formatted(id))
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.customLoader((parent, efh) ->
				{
					var builder = LogicBakingModelData.builder(parent, efh)
							.itemTexture("background", backgroundType.itemBackground())
							.itemTexture("border", backgroundType.itemBorder())
							.itemTexture("icon", LBR.id("item/%s".formatted(id)))
							.boardTexture("background", backgroundType.boardBackground())
							.boardTexture("border", backgroundType.boardBorder());
					if(icon)
					{
						builder.boardTexture("icon", LBR.id("logic/%s".formatted(id)));
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
	
	private void logicComponent(LogicType<?> type, BackgroundType backgroundType, boolean icon)
	{
		this.logicComponent(type, backgroundType, icon, null);
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
