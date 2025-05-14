package net.swedz.little_big_redstone.microchip.object.logic;

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
	 * <p>Builds the logic traversal order for the microchip provided. The order is determined using a slightly modified
	 * version of a Breadth-First Search (BFS) over the circuit's components (which is effectively a graph). We first
	 * add all components with no inputs. Then we add all children components of the starting components to the order
	 * and then mark the wire(s) as "used". A wire that has been used cannot be used again to cause a component to be
	 * added to the order so as to avoid infinitely looping. After adding all children of the component, the children
	 * are iterated over again and then the algorithm is performed recursively over each child component.</p>
	 *
	 * <p>Avoiding cases where an infinite loop would occur, while recursively building if a component that is already
	 * in the order is to be added, all of its children (and subsequent children) will be removed from the order memory
	 * (as well as their corresponding wires) and then re-recursively built to help components that need to run later
	 * be later in the order.</p>
	 *
	 * <p>Lastly, after the initial order has been constructed - there may be duplicate entries. To avoid such
	 * scenarios, we only use the last occurrence of each entry in the order.</p>
	 *
	 * @param microchip the microchip
	 * @return an immutable list containing the logic entries to tick in the order they should tick
	 */
	public static List<LogicEntry> buildOrder(Microchip microchip)
	{
		List<LogicEntry> order = Lists.newArrayList();
		Set<Wire> wiresUsed = Sets.newHashSet();
		
		// Add all of the starting points
		List<LogicEntry> starts = Lists.newArrayList();
		for(var entry : microchip.components())
		{
			if(entry.component().inputs() == 0 || microchip.wires().getByInputSlot(entry.slot()).isEmpty())
			{
				order.add(entry);
				starts.add(entry);
			}
		}
		// Build all the children of the starting points
		for(var entry : starts)
		{
			recursivelyBuildOrder(microchip, entry, order, wiresUsed);
		}
		
		// Only use the last occurrence of each entry
		Set<LogicEntry> touched = Sets.newHashSet();
		List<LogicEntry> finalOrder = Lists.newArrayList();
		for(int i = order.size() - 1; i >= 0; i--)
		{
			var entry = order.get(i);
			if(touched.add(entry))
			{
				finalOrder.addFirst(entry);
			}
		}
		return Collections.unmodifiableList(finalOrder);
	}
	
	private static void recursivelyBuildOrder(Microchip microchip, LogicEntry entry, List<LogicEntry> order, Set<Wire> wiresUsed)
	{
		List<LogicEntry> toBuild = Lists.newArrayList();
		for(var wire : microchip.wires().getByOutputSlot(entry.slot()))
		{
			if(wiresUsed.add(wire))
			{
				var targetEntry = microchip.components().get(wire.input().slot());
				int existingTargetIndex = order.indexOf(targetEntry);
				if(existingTargetIndex >= 0)
				{
					// Prevent infinite loops
					if(hasChild(microchip, targetEntry, true, targetEntry))
					{
						continue;
					}
					// Remove wires after this one so that they can get added by the next call(s) of this method
					microchip.wires().getByOutputSlot(targetEntry.slot()).forEach(wiresUsed::remove);
				}
				order.add(targetEntry);
				toBuild.add(targetEntry);
			}
		}
		for(var targetEntry : toBuild)
		{
			recursivelyBuildOrder(microchip, targetEntry, order, wiresUsed);
		}
	}
	
	private static boolean hasChild(Microchip microchip, LogicEntry entry, boolean recursive, LogicEntry search)
	{
		return hasChild(microchip, entry, recursive, search, Sets.newLinkedHashSet());
	}
	
	private static boolean hasChild(Microchip microchip, LogicEntry entry, boolean recursive, LogicEntry search, Set<LogicEntry> children)
	{
		Set<LogicEntry> found = Sets.newLinkedHashSet();
		for(var wire : microchip.wires().getByOutputSlot(entry.slot()))
		{
			var childEntry = microchip.components().get(wire.input().slot());
			if(childEntry == search)
			{
				return true;
			}
			if(children.add(childEntry))
			{
				found.add(childEntry);
			}
		}
		if(recursive)
		{
			for(var childEntry : found)
			{
				if(hasChild(microchip, childEntry, true, search, children))
				{
					return true;
				}
			}
		}
		return false;
	}
}
