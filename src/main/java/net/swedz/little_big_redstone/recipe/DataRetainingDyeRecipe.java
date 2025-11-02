package net.swedz.little_big_redstone.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.swedz.little_big_redstone.LBRBlocks;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.LBRRecipeTypes;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.item.DyeColoredItem;
import net.swedz.little_big_redstone.item.LogicItem;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class DataRetainingDyeRecipe extends CustomRecipe
{
	public DataRetainingDyeRecipe(CraftingBookCategory category)
	{
		super(category);
	}
	
	private static final Map<TagKey<Item>, ColorableItemTagHandler> TAG_RESULTS = Map.of(
			LBRTags.Items.MICROCHIPS,
			new ColorableItemTagHandler((color) -> LBRBlocks.microchip(color).get()),
			
			LBRTags.Items.LOGIC_COMPONENTS,
			new ColorableItemTagHandler(
					(stack) -> stack.getItem() instanceof LogicItem ? stack.get(LBRComponents.LOGIC).color() : Optional.empty(),
					(original, color) ->
					{
						var output = original.copyWithCount(1);
						var logic = original.get(LBRComponents.LOGIC).copy();
						logic.setColor(color);
						output.set(LBRComponents.LOGIC, logic);
						return output;
					}
			),
			
			LBRTags.Items.LOGIC_ARRAYS,
			new ColorableItemTagHandler(LBRItems::logicArray),
			
			LBRTags.Items.FLOPPY_DISKS,
			new ColorableItemTagHandler(LBRItems::floppyDisk),
			
			LBRTags.Items.STICKY_NOTES,
			new ColorableItemTagHandler(LBRItems::stickyNote)
	);
	
	private record ColorableItemTagHandler(ItemColorGetter colorGetter, ColoredItemFactory coloredItemFactory)
	{
		public ColorableItemTagHandler(ColoredItemFactory coloredItemFactory)
		{
			this(ItemColorGetter.DEFAULT, coloredItemFactory);
		}
		
		public ColorableItemTagHandler(ColoredItemFactory.Simple coloredItemFactory)
		{
			this(ItemColorGetter.DEFAULT, coloredItemFactory);
		}
	}
	
	private interface ItemColorGetter
	{
		ItemColorGetter DEFAULT = (stack) ->
		{
			DyeColoredItem dyeColoredItem;
			if(stack.getItem() instanceof DyeColoredItem item)
			{
				dyeColoredItem = item;
			}
			else if(stack.getItem() instanceof BlockItem blockItem &&
					blockItem.getBlock().asItem() instanceof DyeColoredItem item)
			{
				dyeColoredItem = item;
			}
			else
			{
				return Optional.empty();
			}
			return Optional.of(dyeColoredItem.color());
		};
		
		Optional<DyeColor> get(ItemStack stack);
	}
	
	private interface ColoredItemFactory
	{
		ItemStack create(ItemStack original, Optional<DyeColor> color);
		
		interface Simple extends ColoredItemFactory
		{
			ItemLike create(DyeColor color);
			
			@Override
			default ItemStack create(ItemStack original, Optional<DyeColor> color)
			{
				return color.map((c) -> original.transmuteCopy(this.create(c), 1)).orElse(ItemStack.EMPTY);
			}
		}
	}
	
	public static Set<TagKey<Item>> getAcceptableTags()
	{
		return TAG_RESULTS.keySet();
	}
	
	public static boolean isDyeable(ItemStack stack)
	{
		for(var tag : TAG_RESULTS.keySet())
		{
			if(stack.is(tag))
			{
				return true;
			}
		}
		return false;
	}
	
	public static ItemStack outputVariant(ItemStack stack, Optional<DyeColor> color)
	{
		ItemStack output = ItemStack.EMPTY;
		for(var entry : TAG_RESULTS.entrySet())
		{
			if(stack.is(entry.getKey()))
			{
				var handler = entry.getValue();
				var currentColor = handler.colorGetter().get(stack);
				if(!currentColor.equals(color))
				{
					output = handler.coloredItemFactory().create(stack, color);
					break;
				}
			}
		}
		return output;
	}
	
	private static Optional<Match> find(CraftingInput input)
	{
		ItemStack dyeableItem = ItemStack.EMPTY;
		Optional<DyeColor> color = Optional.empty();
		boolean hasDye = false;
		
		for(int index = 0; index < input.size(); index++)
		{
			var stack = input.getItem(index);
			if(!stack.isEmpty())
			{
				if(isDyeable(stack))
				{
					if(!dyeableItem.isEmpty())
					{
						return Optional.empty();
					}
					dyeableItem = stack;
				}
				else
				{
					if(hasDye)
					{
						return Optional.empty();
					}
					if(stack.getItem() instanceof DyeItem dyeItem)
					{
						color = Optional.of(dyeItem.getDyeColor());
						hasDye = true;
					}
					else if(stack.is(LBRTags.Items.DYE_WASHER))
					{
						hasDye = true;
					}
					else
					{
						return Optional.empty();
					}
				}
			}
		}
		
		if(!dyeableItem.isEmpty() && hasDye)
		{
			var outputVariant = outputVariant(dyeableItem, color);
			if(!outputVariant.isEmpty())
			{
				return Optional.of(new Match(dyeableItem, color, outputVariant));
			}
		}
		return Optional.empty();
	}
	
	@Override
	public boolean matches(CraftingInput input, Level level)
	{
		return find(input).isPresent();
	}
	
	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider)
	{
		return find(input).map(Match::outputVariant).orElse(ItemStack.EMPTY);
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return width * height >= 2;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return LBRRecipeTypes.DATA_RETAINING_DYE_RECIPE_SERIALIZER.get();
	}
	
	private record Match(ItemStack dyeableItem, Optional<DyeColor> color, ItemStack outputVariant)
	{
	}
}
