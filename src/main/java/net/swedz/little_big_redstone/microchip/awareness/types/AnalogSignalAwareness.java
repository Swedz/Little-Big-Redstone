package net.swedz.little_big_redstone.microchip.awareness.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessContext;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAwareness;
import net.swedz.little_big_redstone.microchip.object.logic.reader.LogicReader;
import net.swedz.little_big_redstone.microchip.object.logic.reader.LogicReaderMode;

public final class AnalogSignalAwareness extends MicrochipAwareness<AnalogSignalAwareness>
{
	private boolean[] sides = new boolean[6];
	
	private int[] signals = new int[6];
	
	private boolean[] dirtyNeighborSides = new boolean[]{true, true, true, true, true, true};
	
	public int getSignal(Direction direction)
	{
		return signals[direction.ordinal()];
	}
	
	@Override
	public AwarenessType<AnalogSignalAwareness> type()
	{
		return AwarenessTypes.ANALOG_SIGNAL;
	}
	
	@Override
	public void load(Microchip microchip)
	{
		boolean[] sides = new boolean[6];
		for(var entry : microchip.components())
		{
			if(entry.component() instanceof LogicReader reader)
			{
				var direction = reader.config().direction().ordinal();
				if(sides[direction])
				{
					continue;
				}
				if(reader.config().mode() == LogicReaderMode.COMPARATOR)
				{
					sides[direction] = true;
				}
			}
		}
		this.sides = sides;
	}
	
	@Override
	public void neighborChanged(AwarenessContext context, Block neighborBlock, BlockPos neighborPos, Direction neighborDirection, boolean movedByPiston)
	{
		var level = context.level();
		
		int neighborDirectionIndex = neighborDirection.ordinal();
		if(sides[neighborDirectionIndex])
		{
			var state = level.getBlockState(neighborPos);
			if(state.hasAnalogOutputSignal())
			{
				int signal = state.getAnalogOutputSignal(level, neighborPos, neighborDirection);
				signals[neighborDirectionIndex] = signal;
				return;
			}
		}
		signals[neighborDirectionIndex] = 0;
	}
	
	@Override
	public void neighborBlockEntityChanged(AwarenessContext context, BlockPos neighborPos, Direction neighborDirection)
	{
		int directionIndex = neighborDirection.ordinal();
		if(sides[directionIndex])
		{
			dirtyNeighborSides[directionIndex] = true;
		}
	}
	
	@Override
	public void preTick(AwarenessContext context)
	{
		var level = context.level();
		var pos = context.blockPos();
		
		boolean changed = false;
		int[] signals = new int[6];
		for(int directionIndex = 0; directionIndex < dirtyNeighborSides.length; directionIndex++)
		{
			if(dirtyNeighborSides[directionIndex])
			{
				if(sides[directionIndex])
				{
					var direction = Direction.values()[directionIndex];
					var neighborPos = pos.relative(direction);
					var state = level.getBlockState(neighborPos);
					if(state.hasAnalogOutputSignal())
					{
						int signal = state.getAnalogOutputSignal(level, neighborPos, direction);
						signals[directionIndex] = signal;
						changed = true;
					}
				}
				dirtyNeighborSides[directionIndex] = false;
			}
		}
		if(changed)
		{
			this.signals = signals;
		}
	}
}
