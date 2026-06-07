package net.swedz.little_big_redstone.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.tags.TagKey;
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

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public final class DataRetainingDyeRecipe extends CustomRecipe
{
	public DataRetainingDyeRecipe(CraftingBookCategory category)
	{
		super(category);
	}
	
	private static final Map<TagKey<Item>, ColorableItemTagHandler> TAG_RESULTS = Map.of(
			LBRTags.Items.MICROCHIPS,
			new ColorableItemTagHandler(
					LBRComponents.MICROCHIP_COLOR,
					(color) -> LBRBlocks.microchip(color).get()
			),
			
			LBRTags.Items.LOGIC_COMPONENTS,
			new ColorableItemTagHandler(
					LBRComponents.LOGIC_COLOR,
					(original, color) ->
					{
						var output = original.copyWithCount(1);
						output.set(LBRComponents.LOGIC_COLOR, color.orElse(null));
						return output;
					}
			),
			
			LBRTags.Items.LOGIC_ARRAYS,
			new ColorableItemTagHandler(
					LBRComponents.LOGIC_ARRAY_COLOR,
					LBRItems::logicArray
			),
			
			LBRTags.Items.FLOPPY_DISKS,
			new ColorableItemTagHandler(
					LBRComponents.FLOPPY_DISK_COLOR,
					LBRItems::floppyDisk
			),
			
			LBRTags.Items.STICKY_NOTES,
			new ColorableItemTagHandler(
					LBRComponents.STICKY_NOTE_COLOR,
					LBRItems::stickyNote
			)
	);
	
	private record ColorableItemTagHandler(
			ItemColorGetter colorGetter,
			ColoredItemFactory coloredItemFactory
	)
	{
		public ColorableItemTagHandler(
				Supplier<DataComponentType<DyeColor>> dyeColorComponent,
				ColoredItemFactory coloredItemFactory
		)
		{
			this(ItemColorGetter.forComponent(dyeColorComponent), coloredItemFactory);
		}
		
		public ColorableItemTagHandler(
				Supplier<DataComponentType<DyeColor>> dyeColorComponent,
				ColoredItemFactory.Simple coloredItemFactory
		)
		{
			this(ItemColorGetter.forComponent(dyeColorComponent), coloredItemFactory);
		}
	}
	
	private interface ItemColorGetter
	{
		static ItemColorGetter forComponent(Supplier<DataComponentType<DyeColor>> component)
		{
			return (stack) -> stack.has(component) ?
					Optional.of(stack.get(component)) :
					Optional.empty();
		}
		
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
