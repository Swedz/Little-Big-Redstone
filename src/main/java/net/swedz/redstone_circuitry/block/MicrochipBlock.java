package net.swedz.redstone_circuitry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.redstone_circuitry.blockentity.MicrochipBlockEntity;

public final class MicrochipBlock extends Block implements EntityBlock
{
	public MicrochipBlock(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
	{
		return new MicrochipBlockEntity(blockPos, blockState);
	}
}
