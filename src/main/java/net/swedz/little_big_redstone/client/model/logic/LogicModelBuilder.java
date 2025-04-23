package net.swedz.little_big_redstone.client.model.logic;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.swedz.tesseract.neoforge.api.Assert;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class LogicModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
	public static <T extends ModelBuilder<T>> LogicModelBuilder<T> builder(T parent, ExistingFileHelper existingFileHelper)
	{
		return new LogicModelBuilder<>(parent, existingFileHelper);
	}
	
	private final Map<DyeColor, LogicModelColorSet> colorPalette  = Maps.newHashMap();
	private final LinkedHashMap<String, String>     itemTextures  = Maps.newLinkedHashMap();
	private final LinkedHashMap<String, String>     boardTextures = Maps.newLinkedHashMap();
	
	private LogicModelBuilder(T parent, ExistingFileHelper existingFileHelper)
	{
		super(LogicUnbakedModel.ID, parent, existingFileHelper, false);
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
	
	public LogicModelBuilder<T> itemTexture(String key, ResourceLocation texture)
	{
		Assert.notNull(key);
		Assert.notNull(texture);
		Assert.that(existingFileHelper.exists(texture, ModelProvider.TEXTURE), "Texture %s does not exist".formatted(texture));
		itemTextures.put(key, texture.toString());
		return this;
	}
	
	public LogicModelBuilder<T> boardTexture(String key, ResourceLocation texture)
	{
		Assert.notNull(key);
		Assert.notNull(texture);
		Assert.that(existingFileHelper.exists(texture, ModelProvider.TEXTURE), "Texture %s does not exist".formatted(texture));
		boardTextures.put(key, texture.toString());
		return this;
	}
	
	private static JsonObject toJson(Map<String, String> map)
	{
		JsonObject json = new JsonObject();
		for(var entry : map.entrySet())
		{
			json.addProperty(entry.getKey(), entry.getValue());
		}
		return json;
	}
	
	private static JsonArray toJson(IntSet set)
	{
		JsonArray json = new JsonArray();
		set.intStream().sorted().forEach(json::add);
		return json;
	}
	
	@Override
	public JsonObject toJson(JsonObject json)
	{
		json = super.toJson(json);
		
		JsonObject colorPaletteJson = new JsonObject();
		for(var dyeEntry : colorPalette.entrySet())
		{
			JsonElement dyeJson = LogicModelColorSet.CODEC.encodeStart(JsonOps.INSTANCE, dyeEntry.getValue()).getOrThrow(JsonParseException::new);
			colorPaletteJson.add(dyeEntry.getKey().toString().toLowerCase(Locale.ROOT), dyeJson);
		}
		json.add("color_palette", colorPaletteJson);
		
		JsonObject itemJson = new JsonObject();
		itemJson.add("textures", toJson(itemTextures));
		json.add("item", itemJson);
		
		JsonObject boardJson = new JsonObject();
		boardJson.add("textures", toJson(boardTextures));
		json.add("board", boardJson);
		
		return json;
	}
}
