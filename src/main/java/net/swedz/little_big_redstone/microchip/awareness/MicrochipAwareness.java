package net.swedz.little_big_redstone.microchip.awareness;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.swedz.little_big_redstone.microchip.Microchip;

public abstract class MicrochipAwareness<A extends MicrochipAwareness<A>> implements AwarenessListener
{
	public abstract AwarenessType<A> type();
	
	@Override
	public void load(Microchip microchip)
	{
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
	
	@Override
	public void removed(AwarenessContext context)
	{
	}
}
