package net.swedz.little_big_redstone.block.microchip;

import net.minecraft.core.BlockPos;

/**
 * Injected into {@link net.minecraft.world.entity.player.Player}
 */
public interface MicrochipWatcher
{
	default BlockPos getWatchedMicrochip()
	{
		throw new UnsupportedOperationException("getWatchedMicrochip() must be implemented");
	}
	
	default void setWatchedMicrochip(BlockPos pos)
	{
		throw new UnsupportedOperationException("setWatchedMicrochip() must be implemented");
	}
}
