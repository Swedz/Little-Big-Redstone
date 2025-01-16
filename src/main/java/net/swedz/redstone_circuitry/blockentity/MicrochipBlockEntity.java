package net.swedz.redstone_circuitry.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.redstone_circuitry.RCBlocks;

public final class MicrochipBlockEntity extends BlockEntity
{
	public MicrochipBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(RCBlocks.MICROCHIP_ENTITY.get(), pos, blockState);
	}
}
