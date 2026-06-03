package net.swedz.little_big_redstone.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.NormalCraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.TransmuteRecipe;
import net.minecraft.world.level.Level;

public final class TransmuteWithoutMaterialRecipe extends NormalCraftingRecipe
{
	public static final MapCodec<TransmuteWithoutMaterialRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
					Recipe.CommonInfo.MAP_CODEC.forGetter((recipe) -> recipe.commonInfo),
					CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter((recipe) -> recipe.bookInfo),
					Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> recipe.ingredient),
					ItemStackTemplate.CODEC.fieldOf("result").forGetter((recipe) -> recipe.result)
			)
			.apply(instance, TransmuteWithoutMaterialRecipe::new));
	
	public static final StreamCodec<RegistryFriendlyByteBuf, TransmuteWithoutMaterialRecipe> STREAM_CODEC = StreamCodec.composite(
			Recipe.CommonInfo.STREAM_CODEC, (recipe) -> recipe.commonInfo,
			CraftingRecipe.CraftingBookInfo.STREAM_CODEC, (recipe) -> recipe.bookInfo,
			Ingredient.CONTENTS_STREAM_CODEC, (recipe) -> recipe.ingredient,
			ItemStackTemplate.STREAM_CODEC, (recipe) -> recipe.result,
			TransmuteWithoutMaterialRecipe::new
	);
	
	public static final RecipeSerializer<TransmuteWithoutMaterialRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);
	
	private final Ingredient        ingredient;
	private final ItemStackTemplate result;
	
	public TransmuteWithoutMaterialRecipe(
			Recipe.CommonInfo commonInfo,
			CraftingRecipe.CraftingBookInfo bookInfo,
			Ingredient ingredient,
			ItemStackTemplate result
	)
	{
		super(commonInfo, bookInfo);
		this.ingredient = ingredient;
		this.result = result;
	}
	
	@Override
	public boolean matches(CraftingInput input, Level level)
	{
		return ingredient.test(input.getItem(0));
	}
	
	@Override
	public ItemStack assemble(CraftingInput input)
	{
		for(int index = 0; index < input.size(); index++)
		{
			var stack = input.getItem(index);
			if(!stack.isEmpty() && ingredient.test(stack))
			{
				return TransmuteRecipe.createWithOriginalComponents(result, stack);
			}
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public RecipeSerializer<? extends NormalCraftingRecipe> getSerializer()
	{
		return SERIALIZER;
	}
	
	@Override
	protected PlacementInfo createPlacementInfo()
	{
		return PlacementInfo.create(ingredient);
	}
	
	public static Builder builder(RecipeCategory category, Ingredient ingredient, ItemStackTemplate result)
	{
		return new Builder(category, ingredient, result);
	}
	
	public static final class Builder implements RecipeBuilder
	{
		private final RecipeCategory category;
		private final Ingredient ingredient;
		private final ItemStackTemplate result;
		
		private final RecipeUnlockAdvancementBuilder advancement = new RecipeUnlockAdvancementBuilder();
		
		private String group;
		
		private Builder(RecipeCategory category, Ingredient ingredient, ItemStackTemplate result)
		{
			this.category = category;
			this.ingredient = ingredient;
			this.result = result;
		}
		
		@Override
		public Builder unlockedBy(String name, Criterion<?> criterion)
		{
			advancement.unlockedBy(name, criterion);
			return this;
		}
		
		@Override
		public Builder group(String group)
		{
			this.group = group;
			return this;
		}
		
		@Override
		public ResourceKey<Recipe<?>> defaultId()
		{
			return RecipeBuilder.getDefaultRecipeId(result);
		}
		
		@Override
		public void save(RecipeOutput output, ResourceKey<Recipe<?>> id)
		{
			var recipe = new TransmuteWithoutMaterialRecipe(
					RecipeBuilder.createCraftingCommonInfo(
							true
					),
					RecipeBuilder.createCraftingBookInfo(
							category,
							group
					),
					ingredient,
					result
			);
			output.accept(id, recipe, advancement.build(output, id, category));
		}
	}
}
