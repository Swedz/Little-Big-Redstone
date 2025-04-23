package net.swedz.little_big_redstone.client.model.logic;

import com.google.common.collect.Maps;
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
import net.swedz.little_big_redstone.helper.ModelHelper;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public final class LogicUnbakedModel implements IUnbakedGeometry<LogicUnbakedModel>
{
	public static final ResourceLocation                   ID     = LBR.id("logic");
	public static final IGeometryLoader<LogicUnbakedModel> LOADER = (json, context) ->
	{
		Map<DyeColor, LogicModelColorSet> colorPalette = Maps.newHashMap();
		if(json.has("color_palette"))
		{
			for(var dyeEntry : json.getAsJsonObject("color_palette").entrySet())
			{
				DyeColor dyeColor = DyeColor.valueOf(dyeEntry.getKey().toUpperCase(Locale.ROOT));
				colorPalette.put(dyeColor, LogicModelColorSet.read(dyeEntry.getValue().getAsJsonObject()));
			}
		}
		
		Map<String, Material> itemTextures = Maps.newHashMap();
		if(json.has("item"))
		{
			var itemJson = json.getAsJsonObject("item");
			itemTextures = ModelHelper.gatherTextures(itemJson, "textures");
		}
		
		Map<String, Material> boardTextures = Maps.newHashMap();
		if(json.has("board"))
		{
			var boardJson = json.getAsJsonObject("board");
			boardTextures = ModelHelper.gatherTextures(boardJson, "textures");
		}
		
		return new LogicUnbakedModel(colorPalette, itemTextures, boardTextures);
	};
	
	private static final RenderTypeGroup RENDER_TYPES = new RenderTypeGroup(RenderType.translucent(), NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
	
	private final Map<DyeColor, LogicModelColorSet> colorPalette;
	private final Map<String, Material>             itemTextures;
	private final Map<String, Material>             boardTextures;
	
	private LogicUnbakedModel(Map<DyeColor, LogicModelColorSet> colorPalette,
							  Map<String, Material> itemTextures,
							  Map<String, Material> boardTextures)
	{
		this.colorPalette = Collections.unmodifiableMap(colorPalette);
		this.itemTextures = Collections.unmodifiableMap(itemTextures);
		this.boardTextures = Collections.unmodifiableMap(boardTextures);
	}
	
	private void bakeLayer(CompositeModel.Baked.Builder builder, int index, String texture, ExtraFaceData faceData, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState)
	{
		var sprite = spriteGetter.apply(itemTextures.get(texture));
		var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(index, sprite, faceData);
		var quads = UnbakedGeometryHelper.bakeElements(unbaked, (__) -> sprite, modelState);
		builder.addQuads(RENDER_TYPES, quads);
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
				var colorSet = colorPalette.get(dyeColor);
				var builder = CompositeModel.Baked.builder(context, particle, overrides, context.getTransforms());
				this.bakeLayer(builder, 0, "background", colorSet.backgroundFaceData(), spriteGetter, modelState);
				this.bakeLayer(builder, 1, "border", colorSet.foregroundFaceData(), spriteGetter, modelState);
				this.bakeLayer(builder, 2, "icon", colorSet.foregroundFaceData(), spriteGetter, modelState);
				itemModels.put(dyeColor, builder.build());
			}
			return new LogicBakedModel(colorPalette, itemModels, boardTextures);
		}
		catch (Exception ex)
		{
			LBR.LOGGER.error("Failed to bake logic model {}", context.getModelName(), ex);
			throw ex;
		}
	}
}
