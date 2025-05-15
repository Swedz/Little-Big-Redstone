package net.swedz.little_big_redstone.block.microchip;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.api.TickableBlock;
import net.swedz.little_big_redstone.item.DyeColoredItem;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessContext;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;

public final class MicrochipBlock extends Block implements TickableBlock, DyeColoredItem
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
	
	private final DyeColor color;
	
	public MicrochipBlock(Properties properties, DyeColor color)
	{
		super(properties.isRedstoneConductor(Blocks::never));
		
		this.color = color;
		
		this.registerDefaultState(stateDefinition.any()
				.setValue(UP, false)
				.setValue(DOWN, false)
				.setValue(NORTH, false)
				.setValue(SOUTH, false)
				.setValue(EAST, false)
				.setValue(WEST, false));
	}
	
	@Override
	public DyeColor color()
	{
		return color;
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
			if(level.getBlockEntity(pos) instanceof MicrochipBlockEntity blockEntity)
			{
				blockEntity.openMenu(player);
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
		if(!(level.getBlockEntity(pos) instanceof MicrochipBlockEntity blockEntity))
		{
			return 0;
		}
		
		var redstone = blockEntity.microchip().awarenesses().get(AwarenessTypes.REDSTONE);
		return redstone != null ? redstone.outputRedstoneSignal(state, direction) : 0;
	}
	
	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston)
	{
		if(level.isClientSide() ||
		   !(level.getBlockEntity(pos) instanceof MicrochipBlockEntity blockEntity))
		{
			return;
		}
		
		var delta = neighborPos.subtract(pos);
		Direction neighborDirection = Direction.fromDelta(delta.getX(), delta.getY(), delta.getZ());
		blockEntity.microchip().awarenesses().neighborChanged(new AwarenessContext(blockEntity), neighborBlock, neighborPos, neighborDirection, movedByPiston);
	}
	
	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
	{
		if(!state.is(newState.getBlock()) &&
		   level.getBlockEntity(pos) instanceof MicrochipBlockEntity blockEntity)
		{
			for(var entry : blockEntity.microchip().components())
			{
				var stack = entry.toStack();
				Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
			}
			var redstoneBits = new ItemStack(LBRItems.REDSTONE_BIT, blockEntity.microchip().wires().values().size());
			Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), redstoneBits);
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}
}
