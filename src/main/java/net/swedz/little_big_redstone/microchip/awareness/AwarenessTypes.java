package net.swedz.little_big_redstone.microchip.awareness;

import com.google.common.collect.Maps;
import net.swedz.little_big_redstone.microchip.awareness.types.RedstoneAwareness;

import java.util.Map;

public final class AwarenessTypes
{
	private static final Map<String, AwarenessType> AWARENESSES = Maps.newHashMap();
	
	public static final AwarenessType<RedstoneAwareness> REDSTONE = register("redstone", RedstoneAwareness::new);
	
	private static <A extends MicrochipAwareness> AwarenessType<A> register(String id, AwarenessFactory<A> factory)
	{
		var type = new AwarenessType(id, factory);
		AWARENESSES.put(id, type);
		return type;
	}
	
	public static void init()
	{
	}
}
