package net.swedz.little_big_redstone.client.model.logic;

import com.google.common.collect.Maps;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.ExtraFaceData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientRenderTypes;

import java.util.Map;
import java.util.function.Function;

public final class LogicUnbakedModel implements IUnbakedGeometry<LogicUnbakedModel>
{
	public static final ResourceLocation                   ID     = LBR.id("logic");
	public static final IGeometryLoader<LogicUnbakedModel> LOADER = (json, context) ->
			new LogicUnbakedModel(LogicBakingModelData.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(JsonParseException::new));
	
	private static final RenderTypeGroup NORMAL_RENDER_TYPES   = new RenderTypeGroup(RenderType.translucent(), NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
	private static final RenderTypeGroup SCANLINE_RENDER_TYPES = new RenderTypeGroup(RenderType.translucent(), LBRClientRenderTypes.logicScanline());
	
	private final LogicBakingModelData bakingModelData;
	
	private LogicUnbakedModel(LogicBakingModelData bakingModelData)
	{
		this.bakingModelData = bakingModelData;
	}
	
	private void bakeLayer(IGeometryBakingContext context, TextureAtlasSprite particle, ItemOverrides overrides,
						   CompositeModel.Baked.Builder builder, int index, String texture, ExtraFaceData faceData, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState)
	{
		var innerBuilder = CompositeModel.Baked.builder(context, particle, overrides, context.getTransforms());
		var sprite = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, bakingModelData.getItemTexture(texture)));
		var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(index, sprite, faceData);
		var quads = UnbakedGeometryHelper.bakeElements(unbaked, (__) -> sprite, modelState);
		innerBuilder.addQuads(NORMAL_RENDER_TYPES, quads);
		builder.addLayer(innerBuilder.build());
	}
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
	{
		try
		{
			TextureAtlasSprite particle = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation()));
			var rootTransform = context.getRootTransform();
			if(!rootTransform.isIdentity())
			{
				modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);
			}
			
			Map<DyeColor, BakedModel> itemModels = Maps.newHashMap();
			for(var dyeColor : DyeColor.values())
			{
				var colorSet = bakingModelData.getColorSet(dyeColor);
				var builder = CompositeModel.Baked.builder(context, particle, overrides, context.getTransforms());
				this.bakeLayer(context, particle, overrides, builder, 0, "background", colorSet.backgroundFaceData(), spriteGetter, modelState);
				this.bakeLayer(context, particle, overrides, builder, 1, "border", colorSet.foregroundFaceData(), spriteGetter, modelState);
				this.bakeLayer(context, particle, overrides, builder, 2, "icon", colorSet.foregroundFaceData(), spriteGetter, modelState);
				itemModels.put(dyeColor, builder.build());
			}
			return new LogicBakedModel(bakingModelData, itemModels);
		}
		catch (Exception ex)
		{
			LBR.LOGGER.error("Failed to bake logic model {}", context.getModelName(), ex);
			throw ex;
		}
	}
}
