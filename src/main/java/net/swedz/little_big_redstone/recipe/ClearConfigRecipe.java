package net.swedz.little_big_redstone.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRRecipeTypes;

public final class ClearConfigRecipe extends CustomRecipe
{
	public ClearConfigRecipe(CraftingBookCategory category)
	{
		super(category);
	}
	
	@Override
	public boolean matches(CraftingInput input, Level level)
	{
		boolean hasLogic = false;
		for(var stack : input.items())
		{
			if(stack.has(LBRComponents.LOGIC))
			{
				if(hasLogic)
				{
					return false;
				}
				hasLogic = true;
			}
			else if(!stack.isEmpty())
			{
				return false;
			}
		}
		return hasLogic;
	}
	
	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
	{
		for(var stack : input.items())
		{
			if(stack.has(LBRComponents.LOGIC))
			{
				var copy = stack.copyWithCount(1);
				var logic = stack.get(LBRComponents.LOGIC).copy();
				logic.resetConfig();
				logic.resetColor();
				copy.set(LBRComponents.LOGIC, logic);
				return copy;
			}
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return width * height > 0;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return LBRRecipeTypes.CLEAR_CONFIG_RECIPE_SERIALIZER.get();
	}
}
