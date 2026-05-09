package net.swedz.little_big_redstone.gui.microchip.wire;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
import net.swedz.little_big_redstone.LBRClient;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.wire.Wire;
import net.swedz.tesseract.neoforge.api.Bounds;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public final class WirePathing
{
	private static final ExecutorService PATHFINDER_SERVICE = Executors.newFixedThreadPool(LBRClient.config().microchipWirePathfindingThreads());
	
	public static void shutdownExecutor()
	{
		PATHFINDER_SERVICE.shutdownNow();
	}
	
	private final Microchip microchip;
	
	private final int                      areaPaddingXY;
	private final Function<Bounds, Bounds> componentBoundMutator;
	
	private final Map<Wire, List<Position>> paths = Maps.newHashMap();
	
	public WirePathing(Microchip microchip, int areaPaddingXY, Function<Bounds, Bounds> componentBoundMutator)
	{
		this.microchip = microchip;
		
		this.areaPaddingXY = areaPaddingXY;
		this.componentBoundMutator = componentBoundMutator;
	}
	
	public Bounds mutateComponentBounds(Bounds bounds)
	{
		return componentBoundMutator.apply(bounds);
	}
	
	public List<Position> get(Wire wire, int startX, int startY, int endX, int endY)
	{
		return wire == null ?
				this.build(startX, startY, endX, endY) :
				paths.computeIfAbsent(wire, (__) ->
				{
					List<Position> path = Lists.newArrayList();
					PATHFINDER_SERVICE.execute(() ->
							path.addAll(this.build(startX, startY, endX, endY)));
					return path;
				});
	}
	
	public List<Position> build(int startX, int startY, int endX, int endY, List<Bounds> avoidBounds)
	{
		avoidBounds = Lists.newArrayList(avoidBounds);
		for(var entry : microchip.components())
		{
			if(entry.component().config().isVisible())
			{
				avoidBounds.add(this.mutateComponentBounds(entry.toBounds()));
			}
		}
		return path(startX, startY, endX, endY, microchip, areaPaddingXY, avoidBounds);
	}
	
	public List<Position> build(int startX, int startY, int endX, int endY)
	{
		return this.build(startX, startY, endX, endY, List.of());
	}
	
	public boolean contains(Wire wire, int x, int y, int wireSectionSize, int wireSectionPadding)
	{
		var path = paths.get(wire);
		if(path != null)
		{
			for(var position : path)
			{
				if(x >= position.x - wireSectionPadding - 1 && x <= position.x + wireSectionSize + wireSectionPadding - 1 &&
				   y >= position.y - wireSectionPadding - 1 && y <= position.y + wireSectionSize + wireSectionPadding - 1)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public void forgetEverything()
	{
		paths.clear();
	}
	
	/**
	 * Performs a search to find the best path from the start coordinates to the end coordinates using an A*
	 * implementation. This respects the space of logic components according to the <code>areaPaddingXY</code>
	 * parameter. Paths that overlap a component are not impossible, but are deprioritized significantly.
	 *
	 * @param startX                the start x coordinate
	 * @param startY                the start y coordinate
	 * @param endX                  the end x coordinate
	 * @param endY                  the end y coordinate
	 * @param microchip             the microchip
	 * @param areaPaddingXY         the margin to apply to the full area of the microchip
	 * @param componentBoundMutator the function used to create the bounds for components
	 * @return the list of positions that construct the best path between the points
	 */
	private static List<Position> path(int startX, int startY, int endX, int endY, Microchip microchip, int areaPaddingXY, List<Bounds> avoidBounds)
	{
		var innerBounds = microchip.size().bounds().normalize();
		var bounds = innerBounds.grow(areaPaddingXY, areaPaddingXY);
		var nodes = new NodeGrid(bounds);
		
		var start = nodes.get(startX, startY);
		var end = nodes.get(endX, endY);
		
		if(start == null || end == null)
		{
			return List.of();
		}
		
		var avoidAreas = buildAvoidGrid(innerBounds, bounds, avoidBounds);
		
		ObjectHeapPriorityQueue<Node> open = new ObjectHeapPriorityQueue<>();
		
		open.enqueue(start);
		start.open = true;
		
		while(!open.isEmpty())
		{
			var current = open.dequeue();
			if(current.closed)
			{
				continue;
			}
			current.open = false;
			
			if(current.equals(end))
			{
				return retrace(current);
			}
			
			current.closed = true;
			
			for(var neighbor : neighbors(nodes, current))
			{
				if(neighbor == null || neighbor.closed)
				{
					continue;
				}
				
				int g = current.g + 1 + avoidAreas.getWeight(neighbor);
				
				boolean notOpen = !neighbor.open;
				boolean betterPath = g < neighbor.g;
				if(notOpen || betterPath)
				{
					neighbor.g = g;
					neighbor.h = neighbor.distanceTo(end);
					neighbor.parent = current;
					
					if(notOpen)
					{
						open.enqueue(neighbor);
						neighbor.open = true;
					}
				}
			}
		}
		
		return List.of();
	}
	
	private static AvoidGrid buildAvoidGrid(Bounds innerBounds, Bounds bounds, List<Bounds> avoidBounds)
	{
		// These values are based on the standard microchip gui size and give decent results
		// We dont use the size here because it will yield inconsistent results when rendering smaller microchips
		int mediumAvoidWeight = 160; // 33% of a standard microchip
		int heavyAvoidWeight = 240; // half a standard microchip
		
		var avoidAreas = new AvoidGrid(bounds, mediumAvoidWeight);
		
		int index = Node.indexOf(bounds, innerBounds.minX(), innerBounds.minY());
		for(int y = innerBounds.minY(); y <= innerBounds.maxY(); y++)
		{
			for(int x = innerBounds.minX(); x <= innerBounds.maxX(); x++)
			{
				avoidAreas.setWeight(index, 0);
				index++;
			}
			index += (bounds.width() - innerBounds.width());
		}
		
		for(var avoidBoundsEntry : avoidBounds)
		{
			index = Node.indexOf(bounds, avoidBoundsEntry.minX(), avoidBoundsEntry.minY());
			for(int y = avoidBoundsEntry.minY(); y <= avoidBoundsEntry.maxY(); y++)
			{
				for(int x = avoidBoundsEntry.minX(); x <= avoidBoundsEntry.maxX(); x++)
				{
					avoidAreas.setWeight(index, heavyAvoidWeight);
					index++;
				}
				index += (bounds.width() - avoidBoundsEntry.width());
			}
		}
		
		return avoidAreas;
	}
	
	private static final int[][] NEIGHBOR_DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
	
	private static Node[] neighbors(NodeGrid nodes, Node current)
	{
		Node[] neighbors = new Node[4];
		int index = 0;
		for(int[] direction : NEIGHBOR_DIRECTIONS)
		{
			int x = current.x + direction[0];
			int y = current.y + direction[1];
			neighbors[index] = nodes.get(x, y);
			index++;
		}
		return neighbors;
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
		public static int indexOf(Bounds bounds, int x, int y)
		{
			return bounds.relativeX(x) + bounds.relativeY(y) * bounds.width();
		}
		
		private final int index;
		private final int x, y;
		
		private int g, h;
		
		private Node    parent;
		private boolean open, closed;
		
		public Node(int index, int x, int y)
		{
			this.index = index;
			this.x = x;
			this.y = y;
		}
		
		public Node(Bounds bounds, int x, int y)
		{
			this(indexOf(bounds, x, y), x, y);
		}
		
		public int f()
		{
			return g + h;
		}
		
		public int distanceTo(Node other)
		{
			// This encourages straight lines and diagonals over any other kind of path
			int dx = Math.abs(x - other.x);
			int dy = Math.abs(y - other.y);
			return 10 * (dx + dy) + (14 - 2 * 10) * Math.min(dx, dy);
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
	
	private static final class NodeGrid
	{
		private final Bounds bounds;
		
		private final Node[] nodes;
		
		public NodeGrid(Bounds bounds)
		{
			this.bounds = bounds;
			nodes = new Node[bounds.width() * bounds.height()];
		}
		
		public Node get(int x, int y)
		{
			int index = Node.indexOf(bounds, x, y);
			if(index < 0 || index >= nodes.length)
			{
				return null;
			}
			var node = nodes[index];
			if(node == null)
			{
				node = new Node(index, x, y);
				nodes[index] = node;
			}
			return node;
		}
	}
	
	private static final class AvoidGrid
	{
		private final Bounds bounds;
		
		private final int[] avoids;
		
		public AvoidGrid(Bounds bounds, int defaultValue)
		{
			this.bounds = bounds;
			avoids = new int[bounds.width() * bounds.height()];
			if(defaultValue != 0)
			{
				Arrays.fill(avoids, defaultValue);
			}
		}
		
		public void setWeight(int x, int y, int weight)
		{
			int index = Node.indexOf(bounds, x, y);
			this.setWeight(index, weight);
		}
		
		public void setWeight(int index, int weight)
		{
			if(index < 0 || index >= avoids.length)
			{
				return;
			}
			avoids[index] = weight;
		}
		
		public int getWeight(Node node)
		{
			return avoids[node.index];
		}
	}
}
