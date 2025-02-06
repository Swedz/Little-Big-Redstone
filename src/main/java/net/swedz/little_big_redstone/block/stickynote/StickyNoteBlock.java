package net.swedz.little_big_redstone.block.stickynote;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public final class StickyNoteBlock extends FaceAttachedHorizontalDirectionalBlock implements SimpleWaterloggedBlock, EntityBlock
{
	private static final MapCodec<StickyNoteBlock> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					propertiesCodec(),
					DyeColor.CODEC.fieldOf("color").forGetter(StickyNoteBlock::color)
			)
			.apply(instance, StickyNoteBlock::new));
	
	private static final VoxelShape FLOOR = Block.box(3, 0, 3, 13, 1, 13);
	
	private static final VoxelShape CEILING = Block.box(3, 15, 3, 13, 16, 13);
	
	private static final VoxelShape WALL_NORTH = Block.box(3, 3, 15, 13, 13, 16);
	private static final VoxelShape WALL_SOUTH = Block.box(3, 3, 0, 13, 13, 1);
	private static final VoxelShape WALL_WEST  = Block.box(15, 3, 3, 16, 13, 13);
	private static final VoxelShape WALL_EAST  = Block.box(0, 3, 3, 1, 13, 13);
	
	private final DyeColor color;
	
	public StickyNoteBlock(Properties properties, DyeColor color)
	{
		super(properties);
		
		this.registerDefaultState(stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(FACE, AttachFace.FLOOR)
				.setValue(WATERLOGGED, false));
		
		this.color = color;
	}
	
	public DyeColor color()
	{
		return color;
	}
	
	@Override
	protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec()
	{
		return CODEC;
	}
	
	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		Direction facing = state.getValue(FACING);
		AttachFace face = state.getValue(FACE);
		return switch (face)
		{
			case FLOOR -> FLOOR;
			case WALL -> switch (facing)
			{
				case NORTH -> WALL_NORTH;
				case SOUTH -> WALL_SOUTH;
				case WEST -> WALL_WEST;
				case EAST -> WALL_EAST;
				default -> throw new IllegalStateException("Unexpected value: " + facing);
			};
			case CEILING -> CEILING;
		};
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, FACE, WATERLOGGED);
	}
	
	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
	{
		if(state.getValue(WATERLOGGED))
		{
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		state = super.updateShape(state, facing, facingState, level, currentPos, facingPos);
		return state;
	}
	
	@Override
	protected FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
		return super.getStateForPlacement(context)
				.setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
	}
	
	@Override
	public boolean isPossibleToRespawnInThis(BlockState state)
	{
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
	{
		Direction facing = state.getValue(FACING);
		AttachFace face = state.getValue(FACE);
		
		BlockPos relative;
		if(face == AttachFace.FLOOR)
		{
			relative = pos.below();
		}
		else if(face == AttachFace.CEILING)
		{
			relative = pos.above();
		}
		else
		{
			relative = pos.relative(facing.getOpposite());
		}
		return level.getBlockState(relative).isSolid();
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new StickyNoteBlockEntity(pos, state, color);
	}
	
	@Override
	protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param)
	{
		super.triggerEvent(state, level, pos, id, param);
		BlockEntity blockentity = level.getBlockEntity(pos);
		return blockentity != null && blockentity.triggerEvent(id, param);
	}
	
	@Override
	protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		return blockentity instanceof MenuProvider ? (MenuProvider) blockentity : null;
	}
}
