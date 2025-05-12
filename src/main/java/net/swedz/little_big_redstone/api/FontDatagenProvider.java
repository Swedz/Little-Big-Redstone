package net.swedz.little_big_redstone.api;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.swedz.tesseract.neoforge.api.Assert;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

// TODO move to Tesseract
public abstract class FontDatagenProvider implements DataProvider
{
	private final PackOutput         output;
	private final ExistingFileHelper existingFileHelper;
	private final String             modId, fontName;
	
	private final Map<Character, BitmapCharacterProvider> providers = Maps.newHashMap();
	
	public FontDatagenProvider(PackOutput output, ExistingFileHelper existingFileHelper, String modId, String fontName)
	{
		this.output = output;
		this.existingFileHelper = existingFileHelper;
		this.modId = modId;
		this.fontName = fontName;
	}
	
	protected abstract void addCharacters();
	
	public void addBitmap(char character, ResourceLocation file, int height, int ascent)
	{
		Assert.noneNull(character, file);
		file = file.withPath("%s.png"::formatted);
		Assert.that(existingFileHelper.exists(file.withPrefix("textures/"), PackType.CLIENT_RESOURCES), "Texture %s does not exist in any known resource pack".formatted(file));
		if(providers.put(character, new BitmapCharacterProvider(file, height, ascent)) != null)
		{
			throw new IllegalStateException("Duplicate character " + character);
		}
	}
	
	public void addBitmap(char character, ResourceLocation file)
	{
		this.addBitmap(character, file, 7, 7);
	}
	
	@Override
	public CompletableFuture<?> run(CachedOutput cachedOutput)
	{
		this.addCharacters();
		return !providers.isEmpty() ?
				this.save(cachedOutput, output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(modId).resolve("font").resolve(fontName + ".json")) :
				CompletableFuture.allOf();
	}
	
	private CompletableFuture<?> save(CachedOutput cachedOutput, Path target)
	{
		var json = new JsonObject();
		var providersArray = new JsonArray();
		for(var entry : providers.entrySet())
		{
			var character = entry.getKey();
			var provider = entry.getValue();
			providersArray.add(provider.toJson(character));
		}
		json.add("providers", providersArray);
		return DataProvider.saveStable(cachedOutput, json, target);
	}
	
	@Override
	public String getName()
	{
		return "Font: " + fontName + " for mod: " + modId;
	}
	
	private static final class BitmapCharacterProvider
	{
		private final ResourceLocation file;
		private final int              height, ascent;
		
		public BitmapCharacterProvider(ResourceLocation file, int height, int ascent)
		{
			this.file = file;
			this.height = height;
			this.ascent = ascent;
		}
		
		public JsonObject toJson(char character)
		{
			var json = new JsonObject();
			json.addProperty("type", "bitmap");
			json.addProperty("file", file.toString());
			json.addProperty("height", height);
			json.addProperty("ascent", ascent);
			var chars = new JsonArray();
			chars.add(String.valueOf(character));
			json.add("chars", chars);
			return json;
		}
	}
}
