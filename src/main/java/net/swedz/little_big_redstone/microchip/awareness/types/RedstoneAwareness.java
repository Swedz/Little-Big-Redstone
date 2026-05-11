package net.swedz.little_big_redstone.microchip.awareness.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.neoforged.neoforge.event.EventHooks;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlock;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessContext;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAwareness;
import net.swedz.little_big_redstone.microchip.object.logic.io.LogicIO;
import net.swedz.little_big_redstone.microchip.object.logic.io.LogicPowerOutputType;

import java.util.EnumSet;

public final class RedstoneAwareness extends MicrochipAwareness<RedstoneAwareness>
{
	private boolean initialized;
	
	private boolean[] inputSides        = new boolean[6];
	private boolean[] outputSides       = new boolean[6];
	private boolean[] outputStrongSides = new boolean[6];
	
	private int[] inputPower           = new int[6];
	private int[] outputPower          = new int[6];
	private int[] outputPowerEvaluated = new int[6];
	
	public boolean[] getSides()
	{
		boolean[] sides = new boolean[6];
		for(int index = 0; index < sides.length; index++)
		{
			sides[index] = inputSides[index] || outputSides[index];
		}
		return sides;
	}
	
	public int getInputPower(Direction direction)
	{
		return inputPower[direction.ordinal()];
	}
	
	public int getOutputPower(Direction direction)
	{
		return outputPower[direction.ordinal()];
	}
	
	public void setInputPowered(Direction direction, int signal)
	{
		int index = direction.ordinal();
		if(signal > 0)
		{
			outputPower[index] = 0;
			inputPower[index] = signal;
		}
		else
		{
			inputPower[index] = 0;
		}
	}
	
	public boolean setOutputPowered(Direction direction, int signal)
	{
		int index = direction.ordinal();
		if(signal > outputPowerEvaluated[index])
		{
			inputPower[index] = 0;
			outputPowerEvaluated[index] = signal;
			return true;
		}
		return false;
	}
	
	public int outputRedstoneSignal(BlockState state, Direction direction)
	{
		direction = direction.getOpposite();
		int index = direction.ordinal();
		boolean powered = outputSides[index] && state.getValue(MicrochipBlock.getDirectionalState(direction));
		return powered ? outputPower[index] : 0;
	}
	
	public boolean isOutputStrong(Direction direction)
	{
		direction = direction.getOpposite();
		int index = direction.ordinal();
		return outputStrongSides[index];
	}
	
	@Override
	public AwarenessType<RedstoneAwareness> type()
	{
		return AwarenessTypes.REDSTONE;
	}
	
	@Override
	public void load(Microchip microchip)
	{
		boolean[] inputSides = new boolean[6];
		boolean[] outputSides = new boolean[6];
		boolean[] outputStrongSides = new boolean[6];
		for(var entry : microchip.components())
		{
			if(entry.component() instanceof LogicIO io)
			{
				var direction = io.config().direction.ordinal();
				if(inputSides[direction] || outputSides[direction])
				{
					continue;
				}
				if(io.config().input)
				{
					inputSides[direction] = true;
				}
				else
				{
					outputSides[direction] = true;
					outputStrongSides[direction] = io.config().powerType == LogicPowerOutputType.STRONG;
				}
			}
		}
		this.inputSides = inputSides;
		this.outputSides = outputSides;
		this.outputStrongSides = outputStrongSides;
	}
	
	@Override
	public void neighborChanged(AwarenessContext context, Block neighborBlock, BlockPos neighborPos, Direction neighborDirection, boolean movedByPiston)
	{
		var level = context.level();
		var pos = context.blockPos();
		
		int neighborDirectionIndex = neighborDirection.ordinal();
		if(inputSides[neighborDirectionIndex] && !outputSides[neighborDirectionIndex])
		{
			int signal = level.getSignal(neighborPos, neighborDirection);
			boolean powered = signal > 0;
			this.setInputPowered(neighborDirection, signal);
			level.setBlock(pos, context.state().setValue(MicrochipBlock.getDirectionalState(neighborDirection), powered), Block.UPDATE_ALL);
		}
	}
	
	@Override
	public void preTick(AwarenessContext context)
	{
		var level = context.level();
		var pos = context.blockPos();
		
		if(!initialized)
		{
			int[] inputPower = new int[6];
			for(int directionIndex = 0; directionIndex < inputSides.length; directionIndex++)
			{
				if(inputSides[directionIndex])
				{
					var direction = Direction.values()[directionIndex];
					int signal = level.getSignal(pos.relative(direction), direction);
					inputPower[directionIndex] = signal;
				}
			}
			this.inputPower = inputPower;
			initialized = true;
		}
		
		outputPowerEvaluated = new int[6];
	}
	
	@Override
	public void postTick(AwarenessContext context, boolean microchipDirty, boolean contextDirty)
	{
		var level = context.level();
		var pos = context.blockPos();
		var state = context.state();
		
		boolean[] powerChanges = new boolean[6];
		boolean powerChanged = false;
		for(int index = 0; index < outputPowerEvaluated.length; index++)
		{
			int signal = outputPowerEvaluated[index];
			if(outputPower[index] != signal)
			{
				powerChanges[index] = true;
				powerChanged = true;
			}
		}
		outputPower = outputPowerEvaluated;
		
		var newState = state;
		for(var direction : Direction.values())
		{
			int index = direction.ordinal();
			newState = newState.setValue(MicrochipBlock.getDirectionalState(direction), inputPower[index] > 0 || outputPower[index] > 0);
		}
		if(powerChanged || newState != state)
		{
			level.setBlock(pos, newState, Block.UPDATE_CLIENTS);
			this.sendUpdates(level, pos, state, newState, powerChanges, true);
		}
	}
	
	private boolean[] getUpdateDirectionsForStrongSignal(Direction direction)
	{
		var oppositeDirection = direction.getOpposite();
		boolean[] updateDirections = new boolean[6];
		for(int index = 0; index < 6; index++)
		{
			updateDirections[index] = index != oppositeDirection.ordinal();
		}
		return updateDirections;
	}
	
	/**
	 * <p>Sends neighbor notifications to adjacent blocks. These updates are only sent in the directions of the sides
	 * where an output state has changed. This reduces the number of neighbor notifications that are executed and helps
	 * with situations where microchips are updating frequently.</p>
	 *
	 * @param level           the {@link Level}
	 * @param pos             the {@link BlockPos} of the block updated
	 * @param oldState        the previous {@link BlockState} of the block updated
	 * @param newState        the new {@link BlockState} of the block updated
	 * @param changes         an array of booleans stating which directions (as per the index of {@link Direction#ordinal()})
	 *                        have had changes
	 * @param doStrongUpdates if updates should also be sent to blocks adjacent to the updated blocks
	 * @see ServerLevel#updateNeighborsAt(BlockPos, Block)
	 */
	private void sendUpdates(Level level, BlockPos pos, BlockState oldState, BlockState newState, boolean[] changes, boolean doStrongUpdates)
	{
		var updateDirections = EnumSet.allOf(Direction.class);
		updateDirections.removeIf((direction) -> !changes[direction.ordinal()]);
		EventHooks.onNeighborNotify(level, pos, newState, updateDirections, false);
		
		for(var direction : NeighborUpdater.UPDATE_ORDER)
		{
			int index = direction.ordinal();
			if(changes[index])
			{
				var relativePos = pos.relative(direction);
				level.neighborChanged(relativePos, oldState.getBlock(), pos);
				if(doStrongUpdates && outputStrongSides[index])
				{
					var relativeBlockState = level.getBlockState(relativePos);
					this.sendUpdates(level, relativePos, relativeBlockState, relativeBlockState, this.getUpdateDirectionsForStrongSignal(direction), false);
				}
			}
		}
	}
}
