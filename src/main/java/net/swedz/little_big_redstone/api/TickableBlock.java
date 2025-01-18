package net.swedz.little_big_redstone.api;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface TickableBlock extends EntityBlock
{
	@Override
	default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType)
	{
		return (w, p, s, be) ->
		{
			if(be instanceof Tickable tickable)
			{
				tickable.tick();
			}
		};
	}
}
