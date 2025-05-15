package net.swedz.little_big_redstone.datagen.server.provider.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRBlocks;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapedRecipeBuilder;
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapelessRecipeBuilder;

public final class ItemRecipesDatagenProvider extends RecipeProvider
{
	public ItemRecipesDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider());
	}
	
	private static void microchip(DyeColor color, RecipeOutput output)
	{
		new ShapedRecipeBuilder()
				.pattern("III")
				.pattern("RFR")
				.pattern("INI")
				.define('I', Tags.Items.STORAGE_BLOCKS_IRON)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('F', LBRItems.floppyDisk(color))
				.define('N', Tags.Items.INGOTS_NETHERITE)
				.output(LBRBlocks.microchip(color).get(), 1)
				.offerTo(output, LBR.id("microchip/%s".formatted(color.getName())));
	}
	
	private static void logicArray(DyeColor color, RecipeOutput output)
	{
		new ShapedRecipeBuilder()
				.pattern("III")
				.pattern("DCQ")
				.pattern("III")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('D', color.getTag())
				.define('C', Tags.Items.CHESTS_WOODEN)
				.define('Q', Tags.Items.GEMS_QUARTZ)
				.output(LBRItems.logicArray(color), 1)
				.offerTo(output, LBR.id("logic_array/%s".formatted(color.getName())));
	}
	
	private static void floppyDisk(DyeColor color, RecipeOutput output)
	{
		new ShapedRecipeBuilder()
				.pattern("IQD")
				.pattern("IRI")
				.pattern("IQI")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('Q', Tags.Items.GEMS_QUARTZ)
				.define('D', color.getTag())
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.output(LBRItems.floppyDisk(color), 1)
				.offerTo(output, LBR.id("floppy_disk/%s".formatted(color.getName())));
	}
	
	private static void stickyNote(DyeColor color, RecipeOutput output)
	{
		new ShapelessRecipeBuilder()
				.with(Items.PAPER)
				.with(Tags.Items.SLIME_BALLS)
				.with(Tags.Items.NUGGETS_IRON)
				.with(color.getTag())
				.output(LBRItems.stickyNote(color), 1)
				.offerTo(output, LBR.id("sticky_note/%s".formatted(color.getName())));
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output, HolderLookup.Provider registries)
	{
		for(var color : DyeColor.values())
		{
			microchip(color, output);
			logicArray(color, output);
			floppyDisk(color, output);
			stickyNote(color, output);
		}
		
		new ShapedRecipeBuilder()
				.pattern(" R ")
				.pattern("R R")
				.pattern(" R ")
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.output(LBRItems.REDSTONE_BIT, 8)
				.offerTo(output, LBR.id("redstone_bit"));
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
