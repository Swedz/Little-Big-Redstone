package net.swedz.little_big_redstone.microchip.awareness.types.capability;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAwareness;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class CapabilityAwareness<A extends CapabilityAwareness<A, T>, T> extends MicrochipAwareness<A>
{
	private record CapabilityKey(
			Identifier name,
			Direction direction
	)
	{
	}
	
	private final Cache<CapabilityKey, BlockCapabilityCache<T, Direction>> cache = CacheBuilder.newBuilder()
			.expireAfterAccess(1, TimeUnit.MINUTES)
			.build();
	
	public CapabilityAwareness()
	{
	}
	
	protected abstract List<? extends BlockCapability<T, Direction>> getCapabilities();
	
	private BlockCapabilityCache<T, Direction> create(
			BlockCapability<T, Direction> capability,
			Level level,
			BlockPos pos,
			Direction direction
	)
	{
		return BlockCapabilityCache.create(
				capability,
				(ServerLevel) level,
				pos.relative(direction),
				direction.getOpposite()
		);
	}
	
	public T get(Level level, BlockPos pos, Direction direction)
	{
		try
		{
			for(var capability : this.getCapabilities())
			{
				var key = new CapabilityKey(capability.name(), direction);
				var instance = cache.get(key, () -> this.create(capability, level, pos, direction)).getCapability();
				if(instance != null)
				{
					return instance;
				}
			}
			return null;
		}
		catch(ExecutionException ex)
		{
			LBR.LOGGER.error("Failed to fetch cached capability at {}", pos.toShortString(), ex);
			return null;
		}
	}
}
