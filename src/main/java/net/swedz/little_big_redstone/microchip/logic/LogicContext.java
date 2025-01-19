package net.swedz.little_big_redstone.microchip.logic;

import com.google.common.collect.Sets;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.little_big_redstone.block.MicrochipBlock;

import java.util.Set;

public final class LogicContext
{
	private final Set<Direction> inputPower;
	private final Set<Direction> outputPower;
	
	private boolean dirty;
	
	public LogicContext(Set<Direction> inputPower, Set<Direction> outputPower)
	{
		this.inputPower = inputPower;
		this.outputPower = outputPower;
	}
	
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
	
	public BlockState applyPoweredState(BlockState state)
	{
		for(var direction : Direction.values())
		{
			state = state.setValue(MicrochipBlock.getDirectionalState(direction), inputPower.contains(direction) || outputPower.contains(direction));
		}
		return state;
	}
	
	public boolean isDirty()
	{
		return dirty;
	}
	
	public void markDirty()
	{
		dirty = true;
	}
}
