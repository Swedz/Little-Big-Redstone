package net.swedz.little_big_redstone.client.model.logic;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public final class TextureMap
{
	public static final Codec<TextureMap> CODEC = Codec
			.unboundedMap(Codec.STRING, Identifier.CODEC)
			.xmap(
					TextureMap::new,
					(map) -> map.map
			);
	
	public static final TextureMap EMPTY = new TextureMap(Map.of());
	
	private final Map<String, Identifier> map;
	
	private TextureMap(Map<String, Identifier> map)
	{
		this.map = Collections.unmodifiableMap(map);
	}
	
	public TextureMap put(String key, Identifier texture)
	{
		if(Objects.equals(map.get(key), texture))
		{
			return this;
		}
		Map<String, Identifier> newMap = Maps.newHashMap(map);
		newMap.put(key, texture);
		return new TextureMap(newMap);
	}
	
	public Identifier get(String key)
	{
		return map.getOrDefault(key, MissingTextureAtlasSprite.getLocation());
	}
}
