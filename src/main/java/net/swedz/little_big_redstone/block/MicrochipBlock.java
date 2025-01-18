package net.swedz.little_big_redstone.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.swedz.little_big_redstone.api.TickableBlock;
import net.swedz.little_big_redstone.blockentity.MicrochipBlockEntity;
import net.swedz.little_big_redstone.microchip.Microchip;

public final class MicrochipBlock extends Block implements TickableBlock
{
	public static final BooleanProperty UP    = BooleanProperty.create("up");
	public static final BooleanProperty DOWN  = BooleanProperty.create("down");
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty EAST  = BooleanProperty.create("east");
	public static final BooleanProperty WEST  = BooleanProperty.create("west");
	
	public static BooleanProperty getDirectionalState(Direction direction)
	{
		return switch (direction)
		{
			case UP -> MicrochipBlock.UP;
			case DOWN -> MicrochipBlock.DOWN;
			case NORTH -> MicrochipBlock.NORTH;
			case SOUTH -> MicrochipBlock.SOUTH;
			case EAST -> MicrochipBlock.EAST;
			case WEST -> MicrochipBlock.WEST;
		};
	}
	
	public MicrochipBlock(Properties properties)
	{
		super(properties.isRedstoneConductor(Blocks::never));
		this.registerDefaultState(stateDefinition.any()
				.setValue(UP, false)
				.setValue(DOWN, false)
				.setValue(NORTH, false)
				.setValue(SOUTH, false)
				.setValue(EAST, false)
				.setValue(WEST, false));
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
	{
		return new MicrochipBlockEntity(blockPos, blockState);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST);
	}
	
	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
	{
		if(!level.isClientSide())
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if(blockEntity instanceof MicrochipBlockEntity microchipBlockEntity)
			{
				player.openMenu(microchipBlockEntity, (buf) ->
				{
					buf.writeBlockPos(pos);
					Microchip.STREAM_CODEC.encode(buf, microchipBlockEntity.microchip());
				});
			}
			return InteractionResult.CONSUME;
		}
		return InteractionResult.SUCCESS;
	}
	
	@Override
	protected boolean isSignalSource(BlockState state)
	{
		return true;
	}
	
	@Override
	protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
	{
		return state.getSignal(level, pos, direction);
	}
	
	@Override
	protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
	{
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(!(blockEntity instanceof MicrochipBlockEntity microchipBlockEntity))
		{
			return 0;
		}
		return microchipBlockEntity.isFaceCapableForRedstoneOutput(direction) && state.getValue(getDirectionalState(direction.getOpposite())) ? 15 : 0;
	}
	
	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston)
	{
		if(level.isClientSide())
		{
			return;
		}
		
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(!(blockEntity instanceof MicrochipBlockEntity microchipBlockEntity))
		{
			return;
		}
		
		var delta = neighborPos.subtract(pos);
		Direction direction = Direction.fromDelta(delta.getX(), delta.getY(), delta.getZ());
		if(microchipBlockEntity.isFaceListeningForRedstoneInput(direction) &&
		   !microchipBlockEntity.isFaceCapableForRedstoneOutput(direction))
		{
			boolean signal = level.getSignal(pos.relative(direction), direction) > 0;
			level.setBlock(pos, state.setValue(getDirectionalState(direction), signal), Block.UPDATE_ALL);
		}
	}
}
