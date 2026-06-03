package net.swedz.little_big_redstone;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.recipe.TransmuteWithoutMaterialRecipe;

import java.util.function.Supplier;

public final class LBRRecipes
{
	private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, LBR.ID);
	
	public static final Supplier<RecipeSerializer<?>> TRANSMUTE_WITHOUT_MATERIAL_SERIALIZER = RECIPE_SERIALIZERS.register("transmute_without_material", () -> TransmuteWithoutMaterialRecipe.SERIALIZER);
	
	public static void init(IEventBus bus)
	{
		RECIPE_SERIALIZERS.register(bus);
	}
}
