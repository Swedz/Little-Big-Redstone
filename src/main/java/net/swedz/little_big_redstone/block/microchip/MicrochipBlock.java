package net.swedz.little_big_redstone.block.microchip;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessContext;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.tesseract.neoforge.api.TickableBlock;

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
		return switch(direction)
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
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		super.setPlacedBy(level, pos, state, placer, stack);
		
		if(placer instanceof Player player &&
		   level.getBlockEntity(pos) instanceof MicrochipBlockEntity blockEntity)
		{
			blockEntity.setPlacedBy(player.getUUID());
		}
	}
	
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
	protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
	{
		if(!(level.getBlockEntity(pos) instanceof MicrochipBlockEntity blockEntity))
		{
			return 0;
		}
		
		var redstone = blockEntity.microchip().awarenesses().get(AwarenessTypes.REDSTONE);
		return redstone != null && redstone.isOutputStrong(direction) ? this.getSignal(state, level, pos, direction) : 0;
	}
	
	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston)
	{
		if(level.isClientSide() ||
		   !(level.getBlockEntity(pos) instanceof MicrochipBlockEntity blockEntity))
		{
			return;
		}
		
		// TODO 26.1 i do not like having to this, but they took neighborPos away from me
		for(var neighborDirection : Direction.values())
		{
			var neighborPos = pos.relative(neighborDirection);
			blockEntity.microchip().awarenesses().neighborChanged(new AwarenessContext(blockEntity), neighborBlock, neighborPos, neighborDirection, movedByPiston);
		}
	}
	
	@Override
	public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighborPos)
	{
		if(level.isClientSide() ||
		   !(level.getBlockEntity(pos) instanceof MicrochipBlockEntity blockEntity))
		{
			return;
		}
		
		var delta = neighborPos.subtract(pos);
		var neighborDirection = directionFromDelta(delta.getX(), delta.getY(), delta.getZ());
		if(neighborDirection != null)
		{
			blockEntity.microchip().awarenesses().neighborBlockEntityChanged(new AwarenessContext(blockEntity), neighborPos, neighborDirection);
		}
	}
	
	private static Direction directionFromDelta(int x, int y, int z)
	{
		if(x == 0)
		{
			if(y == 0)
			{
				if(z > 0)
				{
					return Direction.SOUTH;
				}
				if(z < 0)
				{
					return Direction.NORTH;
				}
			}
			else if(z == 0)
			{
				if(y > 0)
				{
					return Direction.UP;
				}
				return Direction.DOWN;
			}
		}
		else if(y == 0 && z == 0)
		{
			if(x > 0)
			{
				return Direction.EAST;
			}
			return Direction.WEST;
		}
		return null;
	}
}
