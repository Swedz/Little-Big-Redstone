package net.swedz.little_big_redstone.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRRecipeTypes;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;

public final class SealStickyNoteRecipe extends CustomRecipe
{
	public SealStickyNoteRecipe(CraftingBookCategory category)
	{
		super(category);
	}
	
	@Override
	public boolean matches(CraftingInput input, Level level)
	{
		boolean hasNote = false;
		boolean hasHoneycomb = false;
		for(var stack : input.items())
		{
			if(stack.getItem() instanceof StickyNoteItem item)
			{
				if(hasNote ||
				   !stack.get(LBRComponents.STICKY_NOTE_EDITABLE))
				{
					return false;
				}
				hasNote = true;
			}
			else if(stack.is(Items.HONEYCOMB))
			{
				if(hasHoneycomb)
				{
					return false;
				}
				hasHoneycomb = true;
			}
			else if(!stack.isEmpty())
			{
				return false;
			}
		}
		return hasNote && hasHoneycomb;
	}
	
	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
	{
		var note = ItemStack.EMPTY;
		boolean hasHoneycomb = false;
		for(var stack : input.items())
		{
			if(stack.getItem() instanceof StickyNoteItem item)
			{
				if(!note.isEmpty() ||
				   !stack.get(LBRComponents.STICKY_NOTE_EDITABLE))
				{
					return ItemStack.EMPTY;
				}
				note = stack;
			}
			else if(stack.is(Items.HONEYCOMB))
			{
				if(hasHoneycomb)
				{
					return ItemStack.EMPTY;
				}
				hasHoneycomb = true;
			}
		}
		
		if(note != null && hasHoneycomb)
		{
			var result = note.copyWithCount(1);
			result.set(LBRComponents.STICKY_NOTE_EDITABLE, false);
			return result;
		}
		
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return width * height > 1;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return LBRRecipeTypes.SEAL_STICKY_NOTE_RECIPE_SERIALIZER.get();
	}
}
