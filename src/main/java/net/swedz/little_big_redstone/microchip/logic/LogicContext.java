package net.swedz.little_big_redstone.microchip.logic;

import com.google.common.collect.Lists;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlock;
import net.swedz.little_big_redstone.microchip.LogicEntry;
import net.swedz.little_big_redstone.microchip.Microchip;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class LogicContext
{
	private final Microchip microchip;
	
	private final Set<Direction> inputPower;
	private final Set<Direction> outputPower;
	
	private final List<LogicEntry> dirtyEntries = Lists.newArrayList();
	
	public LogicContext(Microchip microchip, Set<Direction> inputPower, Set<Direction> outputPower)
	{
		this.microchip = microchip;
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
		return !dirtyEntries.isEmpty();
	}
	
	public void markDirty(LogicComponent component)
	{
		for(var entry : microchip.components())
		{
			if(entry.component() == component)
			{
				dirtyEntries.add(entry);
			}
		}
	}
	
	public List<LogicEntry> getDirtyEntries()
	{
		return Collections.unmodifiableList(dirtyEntries);
	}
}
