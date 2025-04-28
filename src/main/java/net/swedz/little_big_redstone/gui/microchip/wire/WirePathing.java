package net.swedz.little_big_redstone.gui.microchip.wire;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import net.swedz.little_big_redstone.api.Bounds;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.wire.Wire;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;

public final class WirePathing
{
	private final Microchip microchip;
	
	private final int                      areaMarginXY;
	private final Function<Bounds, Bounds> componentBoundMutator;
	
	private final Map<Wire, List<Position>> paths = Maps.newHashMap();
	
	public WirePathing(Microchip microchip, int areaMarginXY, Function<Bounds, Bounds> componentBoundMutator)
	{
		this.microchip = microchip;
		
		this.areaMarginXY = areaMarginXY;
		this.componentBoundMutator = componentBoundMutator;
	}
	
	public List<Position> get(Wire wire, int startX, int startY, int endX, int endY)
	{
		return wire == null ?
				this.build(startX, startY, endX, endY) :
				paths.computeIfAbsent(wire, (__) -> this.build(startX, startY, endX, endY));
	}
	
	private List<Position> build(int startX, int startY, int endX, int endY)
	{
		return path(startX, startY, endX, endY, microchip, areaMarginXY, componentBoundMutator);
	}
	
	public void forgetEverything()
	{
		paths.clear();
	}
	
	/**
	 * Performs a search to find the best path from the start coordinates to the end coordinates using an A*
	 * implementation. This respects the space of logic components according to the <code>componentMarginXY</code>
	 * parameter. Paths that overlap a component are not impossible, but are deprioritized significantly.
	 *
	 * @param startX                the start x coordinate
	 * @param startY                the start y coordinate
	 * @param endX                  the end x coordinate
	 * @param endY                  the end y coordinate
	 * @param microchip             the microchip
	 * @param areaMarginXY          the margin to apply to the full area of the microchip
	 * @param componentBoundMutator the function used to create the bounds for components
	 * @return the list of positions that construct the best path between the points
	 */
	private static List<Position> path(int startX, int startY, int endX, int endY, Microchip microchip, int areaMarginXY, Function<Bounds, Bounds> componentBoundMutator)
	{
		var start = new Node(startX, startY);
		var end = new Node(endX, endY);
		var bounds = microchip.size().bounds().normalize().grow(areaMarginXY, areaMarginXY);
		
		List<Bounds> avoidAreas = Lists.newArrayList();
		for(var entry : microchip.components())
		{
			avoidAreas.add(componentBoundMutator.apply(entry.toBounds()));
		}
		
		PriorityQueue<Node> open = Queues.newPriorityQueue();
		Set<Node> closed = Sets.newHashSet();
		
		open.add(start);
		
		while(!open.isEmpty())
		{
			var current = open.poll();
			
			if(current.equals(end))
			{
				return retrace(current);
			}
			
			closed.add(current);
			
			for(var neighbor : neighbors(current, bounds))
			{
				if(closed.contains(neighbor))
				{
					continue;
				}
				
				int g = current.g + 1;
				for(var area : avoidAreas)
				{
					if(area.contains(neighbor.x, neighbor.y))
					{
						g += 1000;
						break;
					}
				}
				
				boolean openContains = open.contains(neighbor);
				if(!openContains || g < neighbor.g)
				{
					neighbor.g = g;
					neighbor.h = neighbor.distanceTo(end);
					neighbor.parent = current;
					
					if(!openContains)
					{
						open.add(neighbor);
					}
				}
			}
		}
		
		return List.of();
	}
	
	private static List<Node> neighbors(Node current, Bounds bounds)
	{
		List<Node> neighbors = Lists.newArrayList();
		int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
		for(int[] direction : directions)
		{
			int x = current.x + direction[0];
			int y = current.y + direction[1];
			var neighbor = new Node(x, y);
			if(!bounds.contains(x, y))
			{
				continue;
			}
			neighbors.add(neighbor);
		}
		return Collections.unmodifiableList(neighbors);
	}
	
	private static List<Position> retrace(Node end)
	{
		List<Position> path = Lists.newArrayList();
		var current = end;
		while(current != null)
		{
			path.add(current.immutable());
			current = current.parent;
		}
		Collections.reverse(path);
		return Collections.unmodifiableList(path);
	}
	
	public record Position(int x, int y)
	{
	}
	
	private static final class Node implements Comparable<Node>
	{
		private final int x, y;
		
		private int g, h;
		
		private Node parent;
		
		public Node(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		
		public int f()
		{
			return g + h;
		}
		
		public int distanceTo(Node other)
		{
			return (int) (Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
		}
		
		public Position immutable()
		{
			return new Position(x, y);
		}
		
		@Override
		public int compareTo(WirePathing.Node other)
		{
			return Integer.compare(this.f(), other.f());
		}
		
		@Override
		public boolean equals(Object o)
		{
			if(this == o)
			{
				return true;
			}
			if(o == null || this.getClass() != o.getClass())
			{
				return false;
			}
			Node other = (Node) o;
			return x == other.x && y == other.y;
		}
		
		@Override
		public int hashCode()
		{
			return Objects.hash(x, y);
		}
	}
}
