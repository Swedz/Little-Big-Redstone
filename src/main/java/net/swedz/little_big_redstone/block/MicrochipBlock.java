package net.swedz.little_big_redstone.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.swedz.little_big_redstone.blockentity.MicrochipBlockEntity;

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
	
	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
	{
		if(!level.isClientSide())
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if(blockEntity instanceof MicrochipBlockEntity microchipBlockEntity)
			{
				player.openMenu(microchipBlockEntity);
			}
			return InteractionResult.CONSUME;
		}
		return InteractionResult.SUCCESS;
	}
}
