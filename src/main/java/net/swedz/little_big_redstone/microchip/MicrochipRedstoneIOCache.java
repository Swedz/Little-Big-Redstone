package net.swedz.little_big_redstone.microchip;

import com.google.common.collect.Sets;
import net.minecraft.core.Direction;
import net.swedz.little_big_redstone.microchip.logic.io.LogicIO;

import java.util.Set;

public final class MicrochipRedstoneIOCache
{
	private final Microchip microchip;
	
	private Set<Direction> redstoneInputs  = Sets.newHashSet();
	private Set<Direction> redstoneOutputs = Sets.newHashSet();
	
	MicrochipRedstoneIOCache(Microchip microchip)
	{
		this.microchip = microchip;
	}
	
	public void rebuild()
	{
		Set<Direction> inputs = Sets.newHashSet();
		Set<Direction> outputs = Sets.newHashSet();
		for(LogicEntry entry : microchip.components())
		{
			if(entry != null && entry.component() instanceof LogicIO io)
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
		redstoneInputs = inputs;
		redstoneOutputs = outputs;
	}
	
	public boolean isFaceListeningForRedstoneInput(Direction direction)
	{
		return redstoneInputs.contains(direction);
	}
	
	public boolean isFaceCapableForRedstoneOutput(Direction direction)
	{
		return redstoneOutputs.contains(direction);
	}
}
