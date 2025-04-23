package net.swedz.little_big_redstone.helper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.swedz.tesseract.neoforge.api.Assert;

import java.util.LinkedHashMap;
import java.util.List;

public final class ModelHelper
{
	public static LinkedHashMap<String, Material> gatherTextures(JsonObject json, String name)
	{
		Assert.notNull(name);
		if(json == null || !json.has(name))
		{
			return Maps.newLinkedHashMap();
		}
		LinkedHashMap<String, Material> textures = Maps.newLinkedHashMap();
		for(var entry : json.getAsJsonObject(name).entrySet())
		{
			textures.put(entry.getKey(), new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.parse(entry.getValue().getAsString())));
		}
		return textures;
	}
	
	public static List<Material> gatherLayerTextures(JsonObject json, String name)
	{
		Assert.notNull(name);
		if(json == null || !json.has(name))
		{
			return List.of();
		}
		json = json.getAsJsonObject(name);
		ImmutableList.Builder<Material> builder = ImmutableList.builder();
		for(int index = 0; json.has("layer" + index); index++)
		{
			builder.add(new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.parse(json.get("layer" + index).getAsString())));
		}
		return builder.build();
	}
	
	public static List<Material> gatherTextures(IGeometryBakingContext context)
	{
		Assert.notNull(context);
		ImmutableList.Builder<Material> builder = ImmutableList.builder();
		for(int index = 0; context.hasMaterial("layer" + index); index++)
		{
			builder.add(context.getMaterial("layer" + index));
		}
		return builder.build();
	}
}
