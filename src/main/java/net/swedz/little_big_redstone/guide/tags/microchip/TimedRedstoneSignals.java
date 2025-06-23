package net.swedz.little_big_redstone.guide.tags.microchip;

import com.google.common.collect.Maps;
import net.minecraft.core.Direction;
import net.swedz.little_big_redstone.microchip.awareness.types.RedstoneAwareness;

import java.util.Map;

public final class TimedRedstoneSignals
{
	private static final int INTERVAL = 30;
	
	private final int[] fallback = new int[6];
	
	private final Map<Integer, int[]> signals = Maps.newHashMap();
	
	private long tick;
	private int  step;
	
	public void applySignals(RedstoneAwareness redstone)
	{
		int[] signals = this.signals.get(step);
		if(signals == null)
		{
			signals = fallback;
		}
		
		for(var direction : Direction.values())
		{
			int signal = signals[direction.ordinal()];
			redstone.setInputPowered(direction, signal);
		}
	}
	
	public void setSignal(Integer step, Direction direction, int signal)
	{
		int[] signals = step == null ? fallback : this.signals.computeIfAbsent(step, (__) -> new int[6]);
		signals[direction.ordinal()] = signal;
	}
	
	public void tick()
	{
		tick++;
		
		if(tick % INTERVAL == 0)
		{
			tick = 0;
			if(step == signals.size() - 1)
			{
				step = 0;
			}
			else
			{
				step++;
			}
		}
	}
	
	public void reset()
	{
		tick = 0;
		step = 0;
	}
}
