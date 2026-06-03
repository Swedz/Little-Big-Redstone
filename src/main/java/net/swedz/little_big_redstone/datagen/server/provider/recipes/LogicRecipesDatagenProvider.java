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
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapedRecipeBuilder;

import java.util.Map;
import java.util.function.Consumer;

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
	
	private static void logicComponent(RecipeOutput output, LogicType<?, ?> type, Consumer<ShapedRecipeBuilder> action)
	{
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
		
		builder.offerTo(output, LBR.id("logic/%s".formatted(type.id())));
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output, HolderLookup.Provider registries)
	{
		logicComponent(output, LogicTypes.IO, (b) -> b
				.pattern("R  ")
				.pattern(" r ")
				.pattern("  R"));
		
		logicComponent(output, LogicTypes.READER, (b) -> b
				.pattern("R  ")
				.pattern("QrR")
				.pattern("R  "));
		
		logicComponent(output, LogicTypes.TAG, (b) -> b
				.pattern("PQG")
				.pattern("PrR")
				.pattern("PQG"));
		
		logicComponent(output, LogicTypes.NOT, (b) -> b
				.pattern("RrT"));
		
		logicComponent(output, LogicTypes.AND, (b) -> b
				.pattern("T  ")
				.pattern("RrT")
				.pattern("T  "));
		
		logicComponent(output, LogicTypes.NAND, (b) -> b
				.pattern("T  ")
				.pattern("RrR")
				.pattern("T  "));
		
		logicComponent(output, LogicTypes.OR, (b) -> b
				.pattern("R  ")
				.pattern("RrR")
				.pattern("R  "));
		
		logicComponent(output, LogicTypes.NOR, (b) -> b
				.pattern("R  ")
				.pattern("RrT")
				.pattern("R  "));
		
		logicComponent(output, LogicTypes.XOR, (b) -> b
				.pattern("1  ")
				.pattern("Rr2")
				.pattern("2  ")
				.define('1', LBRItems.valueOf("and_gate"))
				.define('2', LBRItems.valueOf("nor_gate")));
		
		logicComponent(output, LogicTypes.SEQUENCER, (b) -> b
				.pattern("GGG")
				.pattern("ErR")
				.pattern("GGG"));
		
		logicComponent(output, LogicTypes.PULSE_THROTTLER, (b) -> b
				.pattern("RrR")
				.pattern(" p "));
		
		logicComponent(output, LogicTypes.SELECTOR, (b) -> b
				.pattern("EGR")
				.pattern("ErR")
				.pattern("EGR"));
		
		logicComponent(output, LogicTypes.RANDOMIZER, (b) -> b
				.pattern("GGR")
				.pattern("1rR")
				.pattern("GGR")
				.define('1', Items.DROPPER));
		
		logicComponent(output, LogicTypes.COMPARATOR, (b) -> b
				.pattern("RT ")
				.pattern("QrT")
				.pattern("RT "));
		
		logicComponent(output, LogicTypes.CALCULATOR, (b) -> b
				.pattern("CCG")
				.pattern("RrQ")
				.pattern("CCG"));
		
		logicComponent(output, LogicTypes.T_FLIP_FLOP, (b) -> b
				.pattern("1  ")
				.pattern("2rR")
				.pattern("1  ")
				.define('1', LBRItems.valueOf("nand_gate"))
				.define('2', LBRItems.valueOf("rs_nor_latch")));
		
		logicComponent(output, LogicTypes.RS_NOR_LATCH, (b) -> b
				.pattern("R1 ")
				.pattern(" rR")
				.pattern("R1 ")
				.define('1', LBRItems.valueOf("nor_gate")));
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
