package net.swedz.little_big_redstone.client.model.stickynote.item;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.swedz.tesseract.neoforge.api.Assert;

import java.util.Map;

public final class StickyNoteItemModelLoaderBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
	public static <T extends ModelBuilder<T>> StickyNoteItemModelLoaderBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper)
	{
		return new StickyNoteItemModelLoaderBuilder<>(parent, existingFileHelper);
	}
	
	private final Map<String, ResourceLocation> itemTextures      = Maps.newHashMap();
	private final Map<String, ResourceLocation> microchipTextures = Maps.newHashMap();
	
	private StickyNoteItemModelLoaderBuilder(T parent, ExistingFileHelper existingFileHelper)
	{
		super(StickyNoteItemUnbakedModel.ID, parent, existingFileHelper, false);
	}
	
	public StickyNoteItemModelLoaderBuilder itemTexture(String key, ResourceLocation texture)
	{
		Assert.noneNull(key, texture);
		itemTextures.put(key, texture);
		return this;
	}
	
	public StickyNoteItemModelLoaderBuilder microchipTexture(String key, ResourceLocation texture)
	{
		Assert.noneNull(key, texture);
		microchipTextures.put(key, texture);
		return this;
	}
	
	private static JsonObject toJson(Map<String, ResourceLocation> models)
	{
		var json = new JsonObject();
		for(var entry : models.entrySet())
		{
			json.addProperty(entry.getKey(), entry.getValue().toString());
		}
		return json;
	}
	
	@Override
	public JsonObject toJson(JsonObject json)
	{
		Assert.that(!itemTextures.isEmpty(), "Item texture layer must have at least one element", JsonParseException::new);
		Assert.that(!microchipTextures.isEmpty(), "Microchip texture layer must have at least one element", JsonParseException::new);
		
		json = super.toJson(json);
		
		json.add("item_textures", toJson(itemTextures));
		json.add("microchip_textures", toJson(microchipTextures));
		
		return json;
	}
}
