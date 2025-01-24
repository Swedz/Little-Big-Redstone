package net.swedz.little_big_redstone.microchip.awareness.types;

import com.google.common.collect.Sets;
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

import java.util.Set;

public final class RedstoneAwareness extends MicrochipAwareness<RedstoneAwareness>
{
	private Set<Direction> inputSides  = Sets.newHashSet();
	private Set<Direction> outputSides = Sets.newHashSet();
	
	private Set<Direction> inputPower  = Sets.newHashSet();
	private Set<Direction> outputPower = Sets.newHashSet();
	
	public boolean isInputPowered(Direction direction)
	{
		return inputPower.contains(direction);
	}
	
	public boolean isOutputPowered(Direction direction)
	{
		return outputPower.contains(direction);
	}
	
	public void setInputPowered(Direction direction, boolean powered)
	{
		if(powered)
		{
			outputPower.remove(direction);
			inputPower.add(direction);
		}
		else
		{
			inputPower.remove(direction);
		}
	}
	
	public void setOutputPowered(Direction direction, boolean powered)
	{
		if(powered)
		{
			inputPower.remove(direction);
			outputPower.add(direction);
		}
		else
		{
			outputPower.remove(direction);
		}
	}
	
	public boolean outputRedstoneSignal(BlockState state, Direction direction)
	{
		direction = direction.getOpposite();
		
		return outputSides.contains(direction) &&
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
		Set<Direction> inputs = Sets.newHashSet();
		Set<Direction> outputs = Sets.newHashSet();
		for(var entry : microchip.components())
		{
			if(entry.component() instanceof LogicIO io)
			{
				var direction = io.config().direction;
				if(inputs.contains(direction) || outputs.contains(direction))
				{
					continue;
				}
				if(io.config().input)
				{
					inputs.add(direction);
				}
				else
				{
					outputs.add(direction);
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
		
		if(inputSides.contains(neighborDirection) && !outputSides.contains(neighborDirection))
		{
			boolean signal = level.getSignal(neighborPos, neighborDirection) > 0;
			level.setBlock(pos, context.state().setValue(MicrochipBlock.getDirectionalState(neighborDirection), signal), Block.UPDATE_ALL);
		}
	}
	
	@Override
	public void preTick(AwarenessContext context)
	{
		var state = context.state();
		
		Set<Direction> inputs = Sets.newHashSet();
		Set<Direction> outputs = Sets.newHashSet();
		for(var direction : Direction.values())
		{
			boolean powered = state.getValue(MicrochipBlock.getDirectionalState(direction));
			if(powered)
			{
				if(inputSides.contains(direction))
				{
					inputs.add(direction);
				}
				else if(outputSides.contains(direction))
				{
					outputs.add(direction);
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
				if(inputSides.contains(direction) && !outputSides.contains(direction) &&
				   level.getSignal(pos.relative(direction), direction) > 0)
				{
					inputPower.add(direction);
				}
			}
		}
		
		for(var direction : Direction.values())
		{
			newState = newState.setValue(MicrochipBlock.getDirectionalState(direction), inputPower.contains(direction) || outputPower.contains(direction));
		}
		if(newState != state)
		{
			level.setBlock(pos, newState, Block.UPDATE_ALL);
		}
	}
}
