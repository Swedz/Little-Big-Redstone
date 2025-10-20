package net.swedz.little_big_redstone;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.recipe.ClearConfigRecipe;
import net.swedz.little_big_redstone.recipe.DataRetainingDyeRecipe;

import java.util.function.Supplier;

public final class LBRRecipeTypes
{
	private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, LBR.ID);
	
	public static final Supplier<RecipeSerializer<DataRetainingDyeRecipe>> DATA_RETAINING_DYE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(
			"crafting_data_retaining_dye",
			() -> new SimpleCraftingRecipeSerializer<>(DataRetainingDyeRecipe::new)
	);
	
	public static final Supplier<RecipeSerializer<ClearConfigRecipe>> CLEAR_CONFIG_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(
			"clear_config",
			() -> new SimpleCraftingRecipeSerializer<>(ClearConfigRecipe::new)
	);
	
	public static void init(IEventBus bus)
	{
		RECIPE_SERIALIZERS.register(bus);
	}
}
