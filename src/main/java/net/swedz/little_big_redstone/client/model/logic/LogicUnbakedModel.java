package net.swedz.little_big_redstone.client.model.logic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
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

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public final class LogicUnbakedModel implements IUnbakedGeometry<LogicUnbakedModel>
{
	public static final ResourceLocation                   ID     = LBR.id("logic");
	public static final IGeometryLoader<LogicUnbakedModel> LOADER = (json, context) ->
	{
		IntSet foregroundLayers = new IntOpenHashSet();
		IntSet backgroundLayers = new IntOpenHashSet();
		Map<DyeColor, LogicModelColorSet> colorPalette = Maps.newHashMap();
		if(json.has("color_palette"))
		{
			var colorPaletteJson = json.getAsJsonObject("color_palette");
			
			if(colorPaletteJson.has("layers"))
			{
				var colorPaletteLayersJson = colorPaletteJson.getAsJsonObject("layers");
				if(colorPaletteLayersJson.has("foreground"))
				{
					for(var element : colorPaletteLayersJson.getAsJsonArray("foreground"))
					{
						foregroundLayers.add(Integer.parseInt(element.getAsString()));
					}
				}
				if(colorPaletteLayersJson.has("background"))
				{
					for(var element : colorPaletteLayersJson.getAsJsonArray("background"))
					{
						backgroundLayers.add(Integer.parseInt(element.getAsString()));
					}
				}
			}
			
			if(colorPaletteJson.has("colors"))
			{
				for(var dyeEntry : colorPaletteJson.getAsJsonObject("colors").entrySet())
				{
					DyeColor dyeColor = DyeColor.valueOf(dyeEntry.getKey().toUpperCase(Locale.ROOT));
					colorPalette.put(dyeColor, LogicModelColorSet.read(dyeEntry.getValue().getAsJsonObject()));
				}
			}
		}
		
		return new LogicUnbakedModel(foregroundLayers, backgroundLayers, colorPalette);
	};
	
	private static final RenderTypeGroup RENDER_TYPES = new RenderTypeGroup(RenderType.translucent(), NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
	
	private final IntSet foregroundLayers, backgroundLayers;
	private final Map<DyeColor, LogicModelColorSet> colorPalette;
	
	private ImmutableList<Material> textures;
	
	private LogicUnbakedModel(IntSet foregroundLayers, IntSet backgroundLayers,
							  Map<DyeColor, LogicModelColorSet> colorPalette)
	{
		this.foregroundLayers = foregroundLayers;
		this.backgroundLayers = backgroundLayers;
		this.colorPalette = colorPalette;
	}
	
	private ImmutableList<Material> gatherTextures(IGeometryBakingContext context)
	{
		ImmutableList.Builder<Material> builder = ImmutableList.builder();
		for(int index = 0; context.hasMaterial("layer" + index); index++)
		{
			builder.add(context.getMaterial("layer" + index));
		}
		return builder.build();
	}
	
	private ImmutableList<ExtraFaceData> gatherFaceData(LogicModelColorSet colorSet)
	{
		ImmutableList.Builder<ExtraFaceData> builder = ImmutableList.builder();
		for(int index = 0; index < textures.size(); index++)
		{
			builder.add(foregroundLayers.contains(index) ? colorSet.foregroundFaceData() : backgroundLayers.contains(index) ? colorSet.backgroundFaceData() : null);
		}
		return builder.build();
	}
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
	{
		try
		{
			if(textures == null)
			{
				textures = this.gatherTextures(context);
			}
			
			TextureAtlasSprite particle = spriteGetter.apply(context.hasMaterial("particle") ? context.getMaterial("particle") : textures.getFirst());
			var rootTransform = context.getRootTransform();
			if(!rootTransform.isIdentity())
			{
				modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);
			}
			
			Map<DyeColor, BakedModel> models = Maps.newHashMap();
			for(var dyeColor : DyeColor.values())
			{
				var colorSet = colorPalette.get(dyeColor);
				var faceData = this.gatherFaceData(colorSet);
				var builder = CompositeModel.Baked.builder(context, particle, overrides, context.getTransforms());
				for(int index = 0; index < textures.size(); index++)
				{
					var sprite = spriteGetter.apply(textures.get(index));
					var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(index, sprite, faceData.get(index));
					var quads = UnbakedGeometryHelper.bakeElements(unbaked, (__) -> sprite, modelState);
					builder.addQuads(RENDER_TYPES, quads);
				}
				models.put(dyeColor, builder.build());
			}
			return new LogicBakedModel(models);
		}
		catch (Exception ex)
		{
			LBR.LOGGER.error("Failed to bake logic model {}", context.getModelName(), ex);
			throw ex;
		}
	}
}
