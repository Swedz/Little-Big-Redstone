package net.swedz.little_big_redstone.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRRecipeTypes;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;

public final class CopyStickyNoteRecipe extends CustomRecipe
{
	public CopyStickyNoteRecipe(CraftingBookCategory category)
	{
		super(category);
	}
	
	@Override
	public boolean matches(CraftingInput input, Level level)
	{
		DyeColor color = null;
		DyeColor textColor = null;
		boolean hasSourceNote = false;
		boolean hasTargetNote = false;
		for(var stack : input.items())
		{
			if(StickyNoteItem.hasRelevantComponents(stack))
			{
				var stackColor = stack.get(LBRComponents.STICKY_NOTE_COLOR);
				if(color != null && !color.equals(stackColor))
				{
					return false;
				}
				color = stackColor;
				
				var stackTextColor = stack.get(LBRComponents.STICKY_NOTE_TEXT_COLOR);
				if(textColor != null && !textColor.equals(stackTextColor))
				{
					return false;
				}
				textColor = stackTextColor;
				
				var note = stack.get(LBRComponents.STICKY_NOTE);
				if(note.isEmpty())
				{
					if(hasTargetNote)
					{
						return false;
					}
					hasTargetNote = true;
				}
				else
				{
					if(hasSourceNote)
					{
						return false;
					}
					hasSourceNote = true;
				}
			}
			else if(!stack.isEmpty())
			{
				return false;
			}
		}
		return hasSourceNote && hasTargetNote;
	}
	
	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
	{
		DyeColor color = null;
		DyeColor textColor = null;
		var source = ItemStack.EMPTY;
		var target = ItemStack.EMPTY;
		for(var stack : input.items())
		{
			if(StickyNoteItem.hasRelevantComponents(stack))
			{
				var stackColor = stack.get(LBRComponents.STICKY_NOTE_COLOR);
				if(color != null && !color.equals(stackColor))
				{
					return ItemStack.EMPTY;
				}
				color = stackColor;
				
				var stackTextColor = stack.get(LBRComponents.STICKY_NOTE_TEXT_COLOR);
				if(textColor != null && !textColor.equals(stackTextColor))
				{
					return ItemStack.EMPTY;
				}
				textColor = stackTextColor;
				
				var note = stack.get(LBRComponents.STICKY_NOTE);
				if(note.isEmpty())
				{
					target = stack;
				}
				else
				{
					source = stack;
				}
			}
		}
		
		if(color != null && textColor != null && !source.isEmpty() && !target.isEmpty())
		{
			var result = source.copyWithCount(2);
			result.set(LBRComponents.STICKY_NOTE_EDITABLE, true);
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
		return LBRRecipeTypes.COPY_STICKY_NOTE_RECIPE_SERIALIZER.get();
	}
}
