package net.swedz.little_big_redstone.datagen.server.provider.recipes;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapedRecipeBuilder;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class LogicRecipesDatagenProvider extends RecipeProvider
{
	public LogicRecipesDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider());
	}
	
	private static final Map<Character, Either<ItemLike, TagKey<Item>>> ITEMS = Map.of(
			'R', Either.right(Tags.Items.DUSTS_REDSTONE),
			'r', Either.left(LBRItems.REDSTONE_BIT),
			'T', Either.left(Items.REDSTONE_TORCH),
			'E', Either.left(Items.REPEATER),
			'Q', Either.right(Tags.Items.GEMS_QUARTZ),
			'G', Either.right(Tags.Items.INGOTS_GOLD),
			'C', Either.right(Tags.Items.INGOTS_COPPER),
			'p', Either.left(Items.STICKY_PISTON),
			'P', Either.right(Tags.Items.ENDER_PEARLS)
	);
	
	private static <L extends LogicComponent<L, C>, C extends LogicConfig<C>> void logicComponent(
			RecipeOutput output,
			Supplier<LogicType<L, C>> typeEntry,
			Consumer<ShapedRecipeBuilder> action
	)
	{
		var type = typeEntry.get();
		
		var builder = new ShapedRecipeBuilder()
				.output(type.item(), 1);
		
		action.accept(builder);
		
		String fullPattern = String.join("", builder.pattern());
		for(var entry : ITEMS.entrySet())
		{
			var key = entry.getKey();
			if(fullPattern.contains(key.toString()))
			{
				entry.getValue().map(
						(item) -> builder.define(key, item),
						(tag) -> builder.define(key, tag)
				);
			}
		}
		
		builder.offerTo(output, LBR.id("logic/%s".formatted(type.id().getPath())));
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output, HolderLookup.Provider registries)
	{
		logicComponent(
				output,
				LBRLogicTypes.IO,
				(b) -> b
						.pattern("R  ")
						.pattern(" r ")
						.pattern("  R")
		);
		
		logicComponent(
				output,
				LBRLogicTypes.READER,
				(b) -> b
						.pattern("R  ")
						.pattern("QrR")
						.pattern("R  ")
		);
		
		logicComponent(
				output,
				LBRLogicTypes.TAG,
				(b) -> b
						.pattern("PQG")
						.pattern("PrR")
						.pattern("PQG")
		);
		
		logicComponent(
				output,
				LBRLogicTypes.NOT,
				(b) -> b
						.pattern("RrT")
		);
		
		logicComponent(
				output,
				LBRLogicTypes.AND,
				(b) -> b
						.pattern("T  ")
						.pattern("RrT")
						.pattern("T  ")
		);
		
		logicComponent(
				output,
				LBRLogicTypes.NAND,
				(b) -> b
						.pattern("T  ")
						.pattern("RrR")
						.pattern("T  ")
		);
		
		logicComponent(
				output,
				LBRLogicTypes.OR,
				(b) -> b
						.pattern("R  ")
						.pattern("RrR")
						.pattern("R  ")
		);
		
		logicComponent(
				output,
				LBRLogicTypes.NOR,
				(b) -> b
						.pattern("R  ")
						.pattern("RrT")
						.pattern("R  ")
		);
		
		logicComponent(
				output,
				LBRLogicTypes.XOR,
				(b) -> b
						.pattern("1  ")
						.pattern("Rr2")
						.pattern("2  ")
						.define('1', LBRItems.valueOf("and_gate"))
						.define('2', LBRItems.valueOf("nor_gate"))
		);
		
		logicComponent(
				output,
				LBRLogicTypes.SEQUENCER,
				(b) -> b
						.pattern("GGG")
						.pattern("ErR")
						.pattern("GGG")
		);
		
		logicComponent(
				output,
				LBRLogicTypes.PULSE_THROTTLER,
				(b) -> b
						.pattern("RrR")
						.pattern(" p ")
		);
		
		logicComponent(
				output,
				LBRLogicTypes.SELECTOR,
				(b) -> b
						.pattern("EGR")
						.pattern("ErR")
						.pattern("EGR")
		);
		
		logicComponent(
				output,
				LBRLogicTypes.RANDOMIZER,
				(b) -> b
						.pattern("GGR")
						.pattern("1rR")
						.pattern("GGR")
						.define('1', Items.DROPPER)
		);
		
		logicComponent(
				output,
				LBRLogicTypes.COMPARATOR,
				(b) -> b
						.pattern("RT ")
						.pattern("QrT")
						.pattern("RT ")
		);
		
		logicComponent(
				output,
				LBRLogicTypes.CALCULATOR,
				(b) -> b
						.pattern("CCG")
						.pattern("RrQ")
						.pattern("CCG")
		);
		
		logicComponent(
				output,
				LBRLogicTypes.T_FLIP_FLOP,
				(b) -> b
						.pattern("1  ")
						.pattern("2rR")
						.pattern("1  ")
						.define('1', LBRItems.valueOf("nand_gate"))
						.define('2', LBRItems.valueOf("rs_nor_latch"))
		);
		
		logicComponent(
				output,
				LBRLogicTypes.RS_NOR_LATCH,
				(b) -> b
						.pattern("R1 ")
						.pattern(" rR")
						.pattern("R1 ")
						.define('1', LBRItems.valueOf("nor_gate"))
		);
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
