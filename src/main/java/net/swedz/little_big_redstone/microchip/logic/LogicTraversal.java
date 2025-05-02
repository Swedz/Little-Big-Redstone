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
		Collections.reverse(order);
		List<LogicEntry> finalOrder = Lists.newArrayList();
		for(var entry : order)
		{
			if(!finalOrder.contains(entry))
			{
				finalOrder.add(entry);
			}
		}
		Collections.reverse(finalOrder);
		return Collections.unmodifiableList(finalOrder);
	}
	
	private static void recursivelyBuildOrder(Microchip microchip, LogicEntry entry, List<LogicEntry> order, Set<Wire> wiresUsed)
	{
		List<LogicEntry> toBuild = Lists.newArrayList();
		outer:
		for(var wire : microchip.wires().getByOutputSlot(entry.slot()))
		{
			if(wiresUsed.add(wire))
			{
				var targetEntry = microchip.components().get(wire.input().slot());
				int existingTargetIndex = order.indexOf(targetEntry);
				if(existingTargetIndex >= 0)
				{
					// Prevent infinite loops
					for(int i = existingTargetIndex + 1; i < order.size(); i++)
					{
						if(order.get(i) == targetEntry)
						{
							continue outer;
						}
					}
					// Remove entries after this one so that they can get added by the next call(s) of this method
					microchip.wires().getByOutputSlot(targetEntry.slot()).forEach(wiresUsed::remove);
					while(existingTargetIndex > order.size())
					{
						order.remove(existingTargetIndex);
					}
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
}
