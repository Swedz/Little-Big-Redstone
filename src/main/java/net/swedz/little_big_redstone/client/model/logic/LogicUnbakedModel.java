package net.swedz.little_big_redstone.client.model.logic;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
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
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.helper.ModelHelper;

import java.util.Collections;
import java.util.List;
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
		
		List<Material> itemTextures = List.of();
		IntSet itemBackgroundLayers = new IntOpenHashSet();
		IntSet itemForegroundLayers = new IntOpenHashSet();
		if(json.has("item"))
		{
			var itemJson = json.getAsJsonObject("item");
			itemTextures = ModelHelper.gatherLayerTextures(itemJson, "textures");
			if(itemJson.has("layers"))
			{
				var colorPaletteLayersJson = itemJson.getAsJsonObject("layers");
				if(colorPaletteLayersJson.has("background"))
				{
					for(var element : colorPaletteLayersJson.getAsJsonArray("background"))
					{
						itemBackgroundLayers.add(Integer.parseInt(element.getAsString()));
					}
				}
				if(colorPaletteLayersJson.has("foreground"))
				{
					for(var element : colorPaletteLayersJson.getAsJsonArray("foreground"))
					{
						itemForegroundLayers.add(Integer.parseInt(element.getAsString()));
					}
				}
			}
		}
		
		Map<String, Material> boardTextures = Maps.newHashMap();
		if(json.has("board"))
		{
			var boardJson = json.getAsJsonObject("board");
			boardTextures = ModelHelper.gatherTextures(boardJson, "textures");
		}
		
		return new LogicUnbakedModel(colorPalette, itemTextures, itemForegroundLayers, itemBackgroundLayers, boardTextures);
	};
	
	private static final RenderTypeGroup RENDER_TYPES = new RenderTypeGroup(RenderType.translucent(), NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
	
	private final Map<DyeColor, LogicModelColorSet> colorPalette;
	
	private final List<Material> itemTextures;
	private final IntSet         itemForegroundLayers, itemBackgroundLayers;
	
	private final Map<String, Material> boardTextures;
	
	private LogicUnbakedModel(Map<DyeColor, LogicModelColorSet> colorPalette,
							  List<Material> itemTextures,
							  IntSet itemForegroundLayers, IntSet itemBackgroundLayers,
							  Map<String, Material> boardTextures)
	{
		this.colorPalette = Collections.unmodifiableMap(colorPalette);
		this.itemTextures = Collections.unmodifiableList(itemTextures);
		this.itemForegroundLayers = IntSets.unmodifiable(itemForegroundLayers);
		this.itemBackgroundLayers = IntSets.unmodifiable(itemBackgroundLayers);
		this.boardTextures = Collections.unmodifiableMap(boardTextures);
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
				for(int index = 0; index < itemTextures.size(); index++)
				{
					var faceData = itemForegroundLayers.contains(index) ? colorSet.foregroundFaceData() : itemBackgroundLayers.contains(index) ? colorSet.backgroundFaceData() : null;
					var sprite = spriteGetter.apply(itemTextures.get(index));
					var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(index, sprite, faceData);
					var quads = UnbakedGeometryHelper.bakeElements(unbaked, (__) -> sprite, modelState);
					builder.addQuads(RENDER_TYPES, quads);
				}
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
