package net.swedz.little_big_redstone.microchip.awareness.types.capability;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessContext;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAwareness;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class CapabilityAwareness<A extends CapabilityAwareness<A, T>, T> extends MicrochipAwareness<A>
{
	private final BlockCapability<T, Direction> capability;
	
	private final Cache<Direction, BlockCapabilityCache<T, Direction>> cache = CacheBuilder.newBuilder()
			.expireAfterAccess(1, TimeUnit.MINUTES)
			.build();
	
	public CapabilityAwareness(BlockCapability<T, Direction> capability)
	{
		this.capability = capability;
	}
	
	private BlockCapabilityCache<T, Direction> create(Level level, BlockPos pos, Direction direction)
	{
		return BlockCapabilityCache.create(
				capability, (ServerLevel) level, pos.relative(direction),
				direction.getOpposite()
		);
	}
	
	public T get(Level level, BlockPos pos, Direction direction)
	{
		try
		{
			return cache.get(direction, () -> this.create(level, pos, direction)).getCapability();
		}
		catch (ExecutionException ex)
		{
			LBR.LOGGER.error("Failed to fetch cached capability at {}", pos.toShortString(), ex);
			return null;
		}
	}
	
	@Override
	public void neighborChanged(AwarenessContext context, Block neighborBlock, BlockPos neighborPos, Direction neighborDirection, boolean movedByPiston)
	{
	}
	
	@Override
	public void preTick(AwarenessContext context)
	{
	}
	
	@Override
	public void postTick(AwarenessContext context, boolean microchipDirty, boolean contextDirty)
	{
	}
}
