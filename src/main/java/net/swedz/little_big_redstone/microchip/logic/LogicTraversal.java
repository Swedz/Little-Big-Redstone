package net.swedz.little_big_redstone.microchip.logic;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.swedz.little_big_redstone.microchip.LogicEntry;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.wire.Wire;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class LogicTraversal
{
	/**
	 * Builds the logic traversal order for the microchip provided. The order is determined using a slightly modified
	 * version of a Breadth-First Search (BFS) over the circuit's components (which is effectively a graph). Starting
	 * at components with no inputs we add all children components to the order and then mark the wire(s) as "used". A
	 * wire that has been used cannot be used again to cause a component to be added to the order so as to avoid
	 * infinitely looping. After adding all children of the component, the children are iterated over again and then
	 * the algorithm is performed recursively over each child component.
	 *
	 * @param microchip the microchip
	 * @return an immutable list containing the logic entries to tick in the order they should tick
	 */
	public static List<LogicEntry> buildOrder(Microchip microchip)
	{
		List<LogicEntry> order = Lists.newArrayList();
		Set<Wire> wiresUsed = Sets.newHashSet();
		
		for(var entry : microchip.components())
		{
			if(entry.component().inputs() == 0 || microchip.wires().getByInputSlot(entry.slot()).isEmpty())
			{
				order.add(entry);
				recursivelyBuildOrder(microchip, entry, order, wiresUsed);
			}
		}
		
		return Collections.unmodifiableList(order);
	}
	
	private static void recursivelyBuildOrder(Microchip microchip, LogicEntry entry, List<LogicEntry> order, Set<Wire> wiresUsed)
	{
		List<LogicEntry> toBuild = Lists.newArrayList();
		for(var wire : microchip.wires().getByOutputSlot(entry.slot()))
		{
			if(wiresUsed.add(wire))
			{
				var targetEntry = microchip.components().get(wire.input().slot());
				order.add(targetEntry);
				toBuild.add(targetEntry);
			}
		}
		for(var targetEntry : toBuild)
		{
			recursivelyBuildOrder(microchip, targetEntry, order, wiresUsed);
		}
	}
}
