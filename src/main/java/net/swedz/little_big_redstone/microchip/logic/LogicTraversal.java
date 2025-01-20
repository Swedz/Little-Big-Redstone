package net.swedz.little_big_redstone.microchip.logic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.swedz.little_big_redstone.microchip.LogicEntry;
import net.swedz.little_big_redstone.microchip.Microchip;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class LogicTraversal
{
	/**
	 * <p>
	 * Builds the logic traversal order for the microchip provided. The below described process ensures that logic
	 * components will not be ticked until all of their parent logic components have been ticked first so that the
	 * output data fetched is accurate.
	 * </p>
	 * <p>
	 * The process starts with components with 0 input ports (i.e. components that take input from the world or
	 * components that lack any wires inputting into it). It then iterates over all of the wires coming out of this
	 * component and increments a counter for each. Once this counter has reached the total amount of wires plugged
	 * into this component, the component will tick. Following this, the process will recursively do the same for
	 * all subsequent components.
	 * </p>
	 *
	 * @param microchip the microchip
	 * @return an immutable list containing the logic entries to tick in the order they should tick
	 */
	public static List<LogicEntry> buildOrder(Microchip microchip)
	{
		List<LogicEntry> order = Lists.newArrayList();
		
		Map<Integer, Integer> componentsPlinged = Maps.newHashMap();
		
		for(var entry : microchip.components())
		{
			if(entry.component().inputs() == 0 || microchip.wires().getByInput(entry.slot()).isEmpty())
			{
				recursivelyBuildOrder(microchip, entry, order, componentsPlinged);
			}
		}
		
		return Collections.unmodifiableList(order);
	}
	
	private static void recursivelyBuildOrder(Microchip microchip, LogicEntry entry, List<LogicEntry> order, Map<Integer, Integer> componentsPlinged)
	{
		order.add(entry);
		for(var wire : microchip.wires().getByOutput(entry.slot()))
		{
			var targetEntry = microchip.components().get(wire.input().slot());
			int plings = componentsPlinged.compute(targetEntry.slot(), (key, value) -> value == null ? 1 : ++value);
			if(plings == microchip.wires().getByInput(targetEntry.slot()).size())
			{
				recursivelyBuildOrder(microchip, targetEntry, order, componentsPlinged);
			}
		}
	}
}
