package net.swedz.little_big_redstone.microchip.logic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.LogicEntry;
import net.swedz.little_big_redstone.microchip.LogicSelectedPort;
import net.swedz.little_big_redstone.microchip.Microchip;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class LogicComponents implements Iterable<LogicEntry>
{
	public static final Codec<LogicComponents> CODEC = Codec.list(LogicEntry.CODEC).xmap(LogicComponents::new, LogicComponents::values);
	
	public static final StreamCodec<ByteBuf, LogicComponents> STREAM_CODEC = LogicEntry.STREAM_CODEC.apply(ByteBufCodecs.list()).map(LogicComponents::new, LogicComponents::values);
	
	private final Microchip microchip;
	
	private Map<Integer, LogicEntry> components;
	
	private List<LogicEntry> traversalOrder;
	
	/**
	 * Should not ever be used directly, only by other constructors.
	 */
	private LogicComponents(Microchip microchip, List<LogicEntry> components)
	{
		this.microchip = microchip;
		this.components = Maps.newHashMap();
		components.forEach((c) ->
		{
			int slot = c.slot();
			if(this.components.containsKey(slot))
			{
				LBR.LOGGER.warn("Duplicate slot id ({}) present in components, skipping!", slot);
				return;
			}
			this.components.put(slot, c);
		});
	}
	
	/**
	 * Should only be used by codecs. Instances created using this should get converted to one that has a microchip attached (using {@link #with(Microchip)}) before doing anything with it.
	 */
	private LogicComponents(List<LogicEntry> components)
	{
		this(null, components);
	}
	
	public LogicComponents(Microchip microchip)
	{
		this(microchip, Lists.newArrayList());
	}
	
	public List<LogicEntry> values()
	{
		return List.copyOf(components.values());
	}
	
	@Override
	public Iterator<LogicEntry> iterator()
	{
		return this.values().iterator();
	}
	
	public List<LogicEntry> traversal()
	{
		return Collections.unmodifiableList(traversalOrder);
	}
	
	public LogicEntry get(int slot)
	{
		return components.get(slot);
	}
	
	public boolean has(int slot)
	{
		return components.containsKey(slot);
	}
	
	public boolean canFit(int x, int y, LogicComponent component)
	{
		for(LogicEntry entry : components.values())
		{
			if(entry.contains(x, y, component.size()))
			{
				return false;
			}
		}
		return true;
	}
	
	public LogicEntry findAt(int x, int y)
	{
		for(LogicEntry entry : components.values())
		{
			if(entry.contains(x, y))
			{
				return entry;
			}
		}
		return null;
	}
	
	public LogicSelectedPort findPortAt(int x, int y, boolean input)
	{
		for(LogicEntry entry : components.values())
		{
			var size = entry.component().size();
			int totalPorts = input ? entry.component().inputs() : entry.component().outputs();
			for(int index = 0; index < totalPorts; index++)
			{
				if(size.portBounds(entry.x(), entry.y(), input, index, totalPorts).contains(x, y))
				{
					return new LogicSelectedPort(entry, index);
				}
			}
		}
		return null;
	}
	
	public LogicEntry set(LogicEntry entry)
	{
		return components.put(entry.slot(), entry);
	}
	
	public LogicEntry set(int slot, int x, int y, LogicComponent component)
	{
		return this.set(new LogicEntry(slot, x, y, component));
	}
	
	private int pickAvailableSlot()
	{
		int slot = 0;
		while(true)
		{
			if(!components.containsKey(slot))
			{
				return slot;
			}
			slot++;
		}
	}
	
	public boolean add(int x, int y, LogicComponent component)
	{
		if(this.canFit(x, y, component))
		{
			this.set(this.pickAvailableSlot(), x, y, component);
			return true;
		}
		return false;
	}
	
	public LogicEntry remove(int slot)
	{
		var original = components.remove(slot);
		microchip.wires().removeAllOutputs(slot);
		for(LogicEntry entry : components.values())
		{
			microchip.wires().removeAllInputs(slot);
		}
		return original;
	}
	
	public LogicEntry remove(LogicEntry entry)
	{
		return this.remove(entry.slot());
	}
	
	public void rebuildTraversal()
	{
		traversalOrder = LogicTraversal.buildOrder(microchip);
	}
	
	public LogicComponents with(Microchip microchip)
	{
		var components = new LogicComponents(microchip);
		components.loadFrom(this);
		return components;
	}
	
	public void loadFrom(LogicComponents other)
	{
		components = other.components;
	}
	
	public void clear()
	{
		components.clear();
		traversalOrder.clear();
	}
}
