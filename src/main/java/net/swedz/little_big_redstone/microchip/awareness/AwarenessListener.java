package net.swedz.little_big_redstone.microchip.awareness;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.swedz.little_big_redstone.microchip.Microchip;

public interface AwarenessListener
{
	void load(Microchip microchip);
	
	void neighborChanged(AwarenessContext context, Block neighborBlock, BlockPos neighborPos, Direction neighborDirection, boolean movedByPiston);
	
	void preTick(AwarenessContext context);
	
	void postTick(AwarenessContext context, boolean microchipDirty, boolean contextDirty);
}
