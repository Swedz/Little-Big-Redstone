package net.swedz.little_big_redstone.client.model.stickynote.entity;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.swedz.little_big_redstone.LBR;

import java.util.function.Function;

public final class StickyNoteEntityUnbakedModel implements IUnbakedGeometry<StickyNoteEntityUnbakedModel>
{
	public static final Identifier                              ID     = LBR.id("sticky_note_entity");
	public static final IGeometryLoader<StickyNoteEntityUnbakedModel> LOADER = (json, context) ->
			new StickyNoteEntityUnbakedModel(loadModels(json, context, "base_layer"), loadModels(json, context, "text_layer"));
	
	private static ImmutableList<BlockModel> loadModels(JsonObject json, JsonDeserializationContext context, String key)
	{
		ImmutableList.Builder<BlockModel> models = ImmutableList.builder();
		if(json.has(key))
		{
			for(var element : json.getAsJsonArray(key))
			{
				models.add(context.<BlockModel>deserialize(element, BlockModel.class));
			}
		}
		return models.build();
	}
	
	private final ImmutableList<BlockModel> baseLayer, textLayer;
	
	private StickyNoteEntityUnbakedModel(ImmutableList<BlockModel> baseLayer, ImmutableList<BlockModel> textLayer)
	{
		this.baseLayer = baseLayer;
		this.textLayer = textLayer;
	}
	
	private static ImmutableList<BakedModel> bakeLayer(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state,
													   ImmutableList<BlockModel> unbakedModels)
	{
		ImmutableList.Builder<BakedModel> bakedModels = ImmutableList.builder();
		for(var unbaked : unbakedModels)
		{
			bakedModels.add(unbaked.bake(baker, spriteGetter, state));
		}
		return bakedModels.build();
	}
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, ItemOverrides overrides)
	{
		return new StickyNoteEntityBakedModel(
				context.getTransforms(),
				context.useAmbientOcclusion(),
				context.isGui3d(),
				context.useBlockLight(),
				spriteGetter.apply(context.getMaterial("particle")),
				bakeLayer(baker, spriteGetter, state, baseLayer),
				bakeLayer(baker, spriteGetter, state, textLayer)
		);
	}
	
	private static void resolveParents(Function<Identifier, UnbakedModel> modelGetter,
									   ImmutableList<BlockModel> unbakedModels)
	{
		for(var model : unbakedModels)
		{
			model.resolveParents(modelGetter);
		}
	}
	
	@Override
	public void resolveParents(Function<Identifier, UnbakedModel> modelGetter, IGeometryBakingContext context)
	{
		resolveParents(modelGetter, baseLayer);
		resolveParents(modelGetter, textLayer);
	}
}
