package net.swedz.little_big_redstone.datagen.server.provider.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.TransmuteRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.TransmuteRecipe;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRBlocks;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapedRecipeBuilder;
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapelessRecipeBuilder;

public final class ItemRecipesDatagenProvider extends RecipeProvider
{
	private ItemRecipesDatagenProvider(HolderLookup.Provider registries, RecipeOutput output)
	{
		super(registries, output);
	}
	
	private void microchip(DyeColor color)
	{
		new ShapedRecipeBuilder(registries)
				.pattern("iIi")
				.pattern("RCR")
				.pattern("iDi")
				.define('I', Tags.Items.STORAGE_BLOCKS_IRON)
				.define('i', Tags.Items.INGOTS_IRON)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('C', LBRItems.REDSTONE_CIRCUIT_BOARD)
				.define('D', color.getTag())
				.output(LBRBlocks.microchip(color).get(), 1)
				.offerTo(output, LBR.id("microchip/" + color.getName()));
		
		this.dyeItem(
				RecipeCategory.REDSTONE,
				LBRTags.Items.MICROCHIPS,
				color,
				LBRBlocks.microchip(color).get().asItem(),
				color,
				"microchip_dye",
				"has_microchip"
		);
	}
	
	private void logicArray(DyeColor color)
	{
		new ShapedRecipeBuilder(registries)
				.pattern("III")
				.pattern("DCQ")
				.pattern("III")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('D', color.getTag())
				.define('C', Tags.Items.CHESTS_WOODEN)
				.define('Q', Tags.Items.GEMS_QUARTZ)
				.output(LBRItems.logicArray(color), 1)
				.offerTo(output, LBR.id("logic_array/" + color.getName()));
		
		this.dyeItem(
				RecipeCategory.REDSTONE,
				LBRTags.Items.LOGIC_ARRAYS,
				color,
				LBRItems.logicArray(color).asItem(),
				color,
				"logic_array_dye",
				"has_logic_array"
		);
	}
	
	private void floppyDisk(DyeColor color)
	{
		new ShapedRecipeBuilder(registries)
				.pattern("IQD")
				.pattern("IRI")
				.pattern("IQI")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('Q', Tags.Items.GEMS_QUARTZ)
				.define('D', color.getTag())
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.output(LBRItems.floppyDisk(color), 1)
				.offerTo(output, LBR.id("floppy_disk/" + color.getName()));
		
		this.dyeItem(
				RecipeCategory.REDSTONE,
				LBRTags.Items.FLOPPY_DISKS,
				color,
				LBRItems.floppyDisk(color).asItem(),
				color,
				"floppy_disk_dye",
				"has_floppy_disk"
		);
	}
	
	private void stickyNote(DyeColor color)
	{
		new ShapelessRecipeBuilder(registries)
				.with(Items.PAPER)
				.with(Tags.Items.SLIME_BALLS)
				.with(Tags.Items.NUGGETS_IRON)
				.with(color.getTag())
				.output(LBRItems.stickyNote(color), 1)
				.offerTo(output, LBR.id("sticky_note/" + color.getName()));
		
		this.dyeItem(
				RecipeCategory.DECORATIONS,
				LBRTags.Items.STICKY_NOTES,
				color,
				LBRItems.stickyNote(color).asItem(),
				color,
				"sticky_note_dye",
				"has_sticky_note"
		);
		
		this.stickyNoteCloning(color);
		this.sealStickyNote(color);
	}
	
	private void dyeLogic(LogicType<?, ?> type, DyeColor color)
	{
		var logicItem = type.item();
		var builder = TransmuteRecipeBuilder
				.transmute(
						RecipeCategory.REDSTONE,
						Ingredient.of(logicItem),
						this.tag(color.getTag()),
						new ItemStackTemplate(
								logicItem,
								DataComponentPatch.builder()
										.set(LBRComponents.LOGIC_COLOR.get(), color)
										.build()
						)
				)
				.group(type.id() + "_logic_dye")
				.unlockedBy("has_" + type.id(), this.has(logicItem));
		builder.save(output, builder.defaultId().identifier().withPrefix("dye/").withSuffix("/" + color.getName()).toString());
	}
	
	private void dyeItem(
			RecipeCategory category,
			TagKey<Item> fromItem,
			DyeColor fromColor,
			Item toItem,
			DyeColor toColor,
			String group,
			String unlockedBy
	)
	{
		var builder = TransmuteRecipeBuilder
				.transmute(
						category,
						this.tag(fromItem),
						this.tag(toColor.getTag()),
						toItem
				)
				.group(group)
				.unlockedBy(unlockedBy, this.has(fromItem));
		builder.save(output, builder.defaultId().identifier().withPrefix("dye/").toString());
	}
	
	private void stickyNoteCloning(DyeColor color)
	{
		var noteItem = LBRItems.stickyNote(color).asItem();
		var onlyBlankStickyNotes = DataComponentIngredient.of(
				false,
				DataComponentPatch.builder()
						.set(LBRComponents.STICKY_NOTE.get(), StickyNote.EMPTY)
						.build(),
				noteItem
		);
		TransmuteRecipeBuilder
				.transmute(
						RecipeCategory.DECORATIONS,
						DifferenceIngredient.of(
								Ingredient.of(noteItem),
								onlyBlankStickyNotes
						),
						onlyBlankStickyNotes,
						new ItemStackTemplate(noteItem)
				)
				.addMaterialCountToOutput()
				.setMaterialCount(TransmuteRecipe.FULL_RANGE_MATERIAL_COUNT)
				.group("sticky_note_cloning")
				.unlockedBy("has_sticky_note", this.has(LBRTags.Items.STICKY_NOTES))
				.save(output, "sticky_note/" + color.getName() + "/cloning");
	}
	
	private void sealStickyNote(DyeColor color)
	{
		var noteItem = LBRItems.stickyNote(color).asItem();
		TransmuteRecipeBuilder
				.transmute(
						RecipeCategory.DECORATIONS,
						DataComponentIngredient.of(
								false,
								DataComponentPatch.builder()
										.set(LBRComponents.STICKY_NOTE_EDITABLE.get(), true)
										.build(),
								noteItem
						),
						this.tag(LBRTags.Items.STICKY_NOTE_SEALANT),
						new ItemStackTemplate(
								noteItem,
								DataComponentPatch.builder()
										.set(LBRComponents.STICKY_NOTE_EDITABLE.get(), false)
										.build()
						)
				)
				.group("sticky_note_sealing")
				.unlockedBy("has_sticky_note", this.has(LBRTags.Items.STICKY_NOTES))
				.save(output, "sticky_note/" + color.getName() + "/sealing");
	}
	
	@Override
	protected void buildRecipes()
	{
		// TODO 26.1 logic clear config
		
		for(var color : DyeColor.values())
		{
			this.microchip(color);
			this.logicArray(color);
			this.floppyDisk(color);
			this.stickyNote(color);
			
			for(var logicType : LogicTypes.values())
			{
				this.dyeLogic(logicType, color);
			}
		}
		
		new ShapedRecipeBuilder(registries)
				.pattern(" R ")
				.pattern("R R")
				.pattern(" R ")
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.output(LBRItems.REDSTONE_BIT, 8)
				.offerTo(output, LBR.id("redstone_bit"));
		
		new ShapedRecipeBuilder(registries)
				.pattern("DdD")
				.pattern("RQR")
				.pattern("DdD")
				.define('D', Items.POLISHED_DEEPSLATE)
				.define('Q', Tags.Items.GEMS_QUARTZ)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('d', Tags.Items.GEMS_DIAMOND)
				.output(LBRItems.REDSTONE_CIRCUIT_BOARD, 1)
				.offerTo(output, LBR.id("redstone_circuit_board"));
	}
	
	public static final class Runner extends RecipeProvider.Runner
	{
		public Runner(GatherDataEvent event)
		{
			super(event.getGenerator().getPackOutput(), event.getLookupProvider());
		}
		
		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output)
		{
			return new ItemRecipesDatagenProvider(registries, output);
		}
		
		@Override
		public String getName()
		{
			return this.getClass().getName();
		}
	}
}
