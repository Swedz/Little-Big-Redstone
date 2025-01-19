package net.swedz.little_big_redstone.microchip.logic;

import com.google.common.collect.Sets;
import net.minecraft.core.Direction;

import java.util.Set;

public final class LogicContext
{
	private final Set<Direction> inputPower  = Sets.newHashSet();
	private final Set<Direction> outputPower = Sets.newHashSet();
	
	private boolean dirty;
	
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
			outputPower.add(direction);
		}
		else
		{
			outputPower.remove(direction);
		}
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
