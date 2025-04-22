package net.swedz.little_big_redstone.client.model.logic;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.swedz.tesseract.neoforge.api.Assert;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public final class LogicModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
	public static <T extends ModelBuilder<T>> LogicModelBuilder<T> builder(T parent, ExistingFileHelper existingFileHelper)
	{
		return new LogicModelBuilder<>(parent, existingFileHelper);
	}
	
	private final IntSet                            foregroundLayers = new IntOpenHashSet();
	private final IntSet                            backgroundLayers = new IntOpenHashSet();
	private final Map<DyeColor, LogicModelColorSet> colorPalette     = Maps.newHashMap();
	
	private LogicModelBuilder(T parent, ExistingFileHelper existingFileHelper)
	{
		super(LogicUnbakedModel.ID, parent, existingFileHelper, false);
	}
	
	public LogicModelBuilder<T> foregroundLayers(int... layers)
	{
		Assert.notNull(layers);
		Assert.that(layers.length > 0);
		Assert.that(Arrays.stream(layers).allMatch((l) -> l >= 0));
		for(int layer : layers)
		{
			foregroundLayers.add(layer);
		}
		return this;
	}
	
	public LogicModelBuilder<T> backgroundLayers(int... layers)
	{
		Assert.notNull(layers);
		Assert.that(layers.length > 0);
		Assert.that(Arrays.stream(layers).allMatch((l) -> l >= 0));
		for(int layer : layers)
		{
			backgroundLayers.add(layer);
		}
		return this;
	}
	
	public LogicModelBuilder<T> foregroundColor(DyeColor dyeColor, int color)
	{
		Assert.noneNull(dyeColor);
		colorPalette.compute(dyeColor, (__, colorSet) ->
		{
			if(colorSet == null)
			{
				colorSet = LogicModelColorSet.DEFAULT;
			}
			return new LogicModelColorSet(color, colorSet.background());
		});
		return this;
	}
	
	public LogicModelBuilder<T> backgroundColor(DyeColor dyeColor, int color)
	{
		Assert.noneNull(dyeColor);
		colorPalette.compute(dyeColor, (__, colorSet) ->
		{
			if(colorSet == null)
			{
				colorSet = LogicModelColorSet.DEFAULT;
			}
			return new LogicModelColorSet(colorSet.foreground(), color);
		});
		return this;
	}
	
	private static JsonArray toJson(IntSet layers)
	{
		JsonArray layersJson = new JsonArray();
		layers.intStream().sorted().forEach(layersJson::add);
		return layersJson;
	}
	
	@Override
	public JsonObject toJson(JsonObject json)
	{
		json = super.toJson(json);
		
		JsonObject colorPaletteJson = new JsonObject();
		
		JsonObject colorPaletteLayersJson = new JsonObject();
		colorPaletteLayersJson.add("foreground", toJson(foregroundLayers));
		colorPaletteLayersJson.add("background", toJson(backgroundLayers));
		colorPaletteJson.add("layers", colorPaletteLayersJson);
		
		JsonObject colorPaletteColorsJson = new JsonObject();
		for(var dyeEntry : colorPalette.entrySet())
		{
			JsonElement dyeJson = LogicModelColorSet.CODEC.encodeStart(JsonOps.INSTANCE, dyeEntry.getValue()).getOrThrow(JsonParseException::new);
			colorPaletteColorsJson.add(dyeEntry.getKey().toString().toLowerCase(Locale.ROOT), dyeJson);
		}
		colorPaletteJson.add("colors", colorPaletteColorsJson);
		
		json.add("color_palette", colorPaletteJson);
		
		return json;
	}
}
