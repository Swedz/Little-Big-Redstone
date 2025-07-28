package net.swedz.little_big_redstone.block.channel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.swedz.little_big_redstone.LBRBlocks;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.awareness.types.RedstoneAwareness;
import net.swedz.tesseract.neoforge.api.Assert;

public final class ChannelBlock extends Block
{
	public static final DirectionProperty ORIGIN_DIRECTION = DirectionProperty.create("origin_direction");
	public static final BooleanProperty   INPUT            = BooleanProperty.create("input");
	public static final IntegerProperty   CHANNEL          = IntegerProperty.create("channel", 1, 15);
	public static final IntegerProperty   POWER            = IntegerProperty.create("power", 0, 15);
	
	public static boolean is(BlockState state, Direction originDirection)
	{
		return state.is(LBRBlocks.CHANNEL.get()) &&
			   state.getValue(ORIGIN_DIRECTION) == originDirection;
	}
	
	public static boolean is(BlockState state, Direction originDirection, int channel)
	{
		return is(state, originDirection) &&
			   state.getValue(CHANNEL) == channel;
	}
	
	private static MicrochipBlockEntity getOrigin(Level level, BlockPos pos, BlockState state)
	{
		Assert.that(state.is(LBRBlocks.CHANNEL.get()), "Cannot get origin direction for non-channel block");
		
		var originDirection = state.getValue(ORIGIN_DIRECTION);
		var channelIndex = state.getValue(CHANNEL);
		var originPosition = pos.relative(originDirection, channelIndex);
		if(level.getBlockEntity(originPosition) instanceof MicrochipBlockEntity microchip)
		{
			return microchip;
		}
		return null;
	}
	
	private static RedstoneAwareness getOriginRedstone(Level level, BlockPos pos, BlockState state)
	{
		var microchip = getOrigin(level, pos, state);
		return microchip != null ? microchip.microchip().awarenesses().get(AwarenessTypes.REDSTONE) : null;
	}
	
	public ChannelBlock(Properties properties)
	{
		super(properties.isRedstoneConductor(Blocks::never));
		
		this.registerDefaultState(stateDefinition.any()
				.setValue(ORIGIN_DIRECTION, Direction.NORTH)
				.setValue(INPUT, true)
				.setValue(CHANNEL, 1)
				.setValue(POWER, 0));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(ORIGIN_DIRECTION);
		builder.add(INPUT);
		builder.add(CHANNEL);
		builder.add(POWER);
	}
	
	private static int calculateChannelIndex(Level level, BlockPos channelPos, Direction originDirection)
	{
		var adjacentPos = channelPos.relative(originDirection);
		var adjacentBlock = level.getBlockState(adjacentPos);
		if(is(adjacentBlock, originDirection))
		{
			int previousChannelIndex = adjacentBlock.getValue(CHANNEL);
			return Math.min(previousChannelIndex + 1, 15);
		}
		return 1;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		var face = context.getClickedFace();
		var originDirection = face.getOpposite();
		var channelPos = context.getClickedPos();
		return this.defaultBlockState()
				.setValue(ORIGIN_DIRECTION, originDirection)
				.setValue(CHANNEL, calculateChannelIndex(context.getLevel(), channelPos, originDirection));
	}
	
	@Override
	protected BlockState rotate(BlockState state, Rotation rotation)
	{
		// TODO recalculate channel index, but we cant get level or pos here???
		return RotatedPillarBlock.rotatePillar(state, rotation);
	}
	
	@Override
	protected boolean isSignalSource(BlockState state)
	{
		return !state.getValue(INPUT);
	}
	
	@Override
	protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
	{
		return state.getValue(ORIGIN_DIRECTION).getAxis() == direction.getAxis() || state.getValue(INPUT) ? 0 : state.getValue(POWER);
	}
	
	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston)
	{
		if(level.isClientSide())
		{
			return;
		}
		
		var originalState = state;
		
		var delta = neighborPos.subtract(pos);
		Direction neighborDirection = Direction.fromDelta(delta.getX(), delta.getY(), delta.getZ());
		
		// The block that updated is the block in the direction the channel is pointing
		if(is(state, neighborDirection))
		{
			int channel = calculateChannelIndex(level, pos, neighborDirection);
			state = state.setValue(CHANNEL, channel);
			
			var channelDirection = state.getValue(ORIGIN_DIRECTION).getOpposite();
			
			boolean input = true;
			int power = 0;
			var redstone = getOriginRedstone(level, pos, state);
			if(redstone != null)
			{
				input = !redstone.isOutput(channelDirection, channel);
				power = redstone.getOutputPower(channelDirection, channel);
			}
			state = state
					.setValue(INPUT, input)
					.setValue(POWER, power);
		}
		// The block that updated is not on either end of the channel
		else if(!is(state, neighborDirection.getOpposite()))
		{
			var redstone = getOriginRedstone(level, pos, state);
			if(redstone != null)
			{
				state = redstone.updateSignal(level, pos, state, neighborPos, neighborDirection, state.getValue(CHANNEL), state.getValue(ORIGIN_DIRECTION).getOpposite());
			}
			else
			{
				int signal = level.getSignal(neighborPos, neighborDirection);
				state = state.setValue(ChannelBlock.POWER, signal);
			}
		}
		
		if(state != originalState)
		{
			level.setBlock(pos, state, Block.UPDATE_ALL);
		}
		super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
	}
}
