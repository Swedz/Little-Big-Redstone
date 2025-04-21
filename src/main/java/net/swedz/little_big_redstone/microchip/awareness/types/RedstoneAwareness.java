package net.swedz.little_big_redstone.microchip.awareness.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlock;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessContext;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAwareness;
import net.swedz.little_big_redstone.microchip.logic.io.LogicIO;

public final class RedstoneAwareness extends MicrochipAwareness<RedstoneAwareness>
{
	private boolean[] inputSides  = new boolean[6];
	private boolean[] outputSides = new boolean[6];
	
	private boolean[] inputPower  = new boolean[6];
	private boolean[] outputPower = new boolean[6];
	
	public boolean[] getSides()
	{
		boolean[] sides = new boolean[6];
		for(int index = 0; index < sides.length; index++)
		{
			sides[index] = inputSides[index] || outputSides[index];
		}
		return sides;
	}
	
	public boolean isInputPowered(Direction direction)
	{
		return inputPower[direction.ordinal()];
	}
	
	public boolean isOutputPowered(Direction direction)
	{
		return outputPower[direction.ordinal()];
	}
	
	public void setInputPowered(Direction direction, boolean powered)
	{
		int index = direction.ordinal();
		if(powered)
		{
			outputPower[index] = false;
			inputPower[index] = true;
		}
		else
		{
			inputPower[index] = false;
		}
	}
	
	public void setOutputPowered(Direction direction, boolean powered)
	{
		int index = direction.ordinal();
		if(powered)
		{
			inputPower[index] = false;
			outputPower[index] = true;
		}
		else
		{
			outputPower[index] = false;
		}
	}
	
	public boolean outputRedstoneSignal(BlockState state, Direction direction)
	{
		direction = direction.getOpposite();
		
		return outputSides[direction.ordinal()] &&
			   state.getValue(MicrochipBlock.getDirectionalState(direction));
	}
	
	@Override
	public AwarenessType<RedstoneAwareness> type()
	{
		return AwarenessTypes.REDSTONE;
	}
	
	@Override
	public void load(Microchip microchip)
	{
		boolean[] inputs = new boolean[6];
		boolean[] outputs = new boolean[6];
		for(var entry : microchip.components())
		{
			if(entry.component() instanceof LogicIO io)
			{
				var direction = io.config().direction.ordinal();
				if(inputs[direction] || outputs[direction])
				{
					continue;
				}
				if(io.config().input)
				{
					inputs[direction] = true;
				}
				else
				{
					outputs[direction] = true;
				}
			}
		}
		inputSides = inputs;
		outputSides = outputs;
	}
	
	@Override
	public void neighborChanged(AwarenessContext context, Block neighborBlock, BlockPos neighborPos, Direction neighborDirection, boolean movedByPiston)
	{
		var level = context.level();
		var pos = context.pos();
		
		int neighborDirectionIndex = neighborDirection.ordinal();
		if(inputSides[neighborDirectionIndex] && !outputSides[neighborDirectionIndex])
		{
			boolean signal = level.getSignal(neighborPos, neighborDirection) > 0;
			level.setBlock(pos, context.state().setValue(MicrochipBlock.getDirectionalState(neighborDirection), signal), Block.UPDATE_ALL);
		}
	}
	
	@Override
	public void preTick(AwarenessContext context)
	{
		var state = context.state();
		
		boolean[] inputs = new boolean[6];
		boolean[] outputs = new boolean[6];
		for(var direction : Direction.values())
		{
			int index = direction.ordinal();
			boolean powered = state.getValue(MicrochipBlock.getDirectionalState(direction));
			if(powered)
			{
				if(inputSides[index])
				{
					inputs[index] = true;
				}
				else if(outputSides[index])
				{
					outputs[index] = true;
				}
			}
		}
		inputPower = inputs;
		outputPower = outputs;
	}
	
	@Override
	public void postTick(AwarenessContext context, boolean microchipDirty, boolean contextDirty)
	{
		var level = context.level();
		var pos = context.pos();
		var state = context.state();
		
		var newState = state;
		
		if(microchipDirty)
		{
			for(var direction : Direction.values())
			{
				int index = direction.ordinal();
				if(inputSides[index] && !outputSides[index] &&
				   level.getSignal(pos.relative(direction), direction) > 0)
				{
					inputPower[index] = true;
				}
			}
		}
		
		for(var direction : Direction.values())
		{
			int index = direction.ordinal();
			newState = newState.setValue(MicrochipBlock.getDirectionalState(direction), inputPower[index] || outputPower[index]);
		}
		if(newState != state)
		{
			level.setBlock(pos, newState, Block.UPDATE_ALL);
		}
	}
}
