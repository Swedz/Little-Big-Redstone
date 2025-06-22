package net.swedz.little_big_redstone.microchip.object.logic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.object.MicrochipObjectContainer;
import net.swedz.little_big_redstone.microchip.wire.Wire;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class LogicComponents extends MicrochipObjectContainer<LogicEntry, LogicComponents>
{
	public static final Codec<LogicComponents> CODEC = Codec.list(LogicEntry.CODEC).xmap(LogicComponents::new, LogicComponents::values);
	
	public static final StreamCodec<ByteBuf, LogicComponents> STREAM_CODEC = LogicEntry.STREAM_CODEC.apply(ByteBufCodecs.list()).map(LogicComponents::new, LogicComponents::values);
	
	private List<LogicEntry> traversalOrder = List.of();
	private boolean          debug;
	
	/**
	 * Should not ever be used directly, only by other constructors.
	 */
	private LogicComponents(Microchip microchip, List<LogicEntry> components)
	{
		super(microchip, components);
		for(var entry : components)
		{
			if(entry.component().type() == LogicTypes.DEBUGGER)
			{
				debug = true;
				break;
			}
		}
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
	
	public List<LogicEntry> traversal()
	{
		return traversalOrder;
	}
	
	public boolean isDebug()
	{
		return debug;
	}
	
	public LogicSelectedPort findPortAt(int x, int y, boolean input)
	{
		for(LogicEntry entry : this.values())
		{
			var size = entry.size();
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
	
	public LogicEntry add(int x, int y, LogicComponent component)
	{
		if(microchip.canFit(component.size().toBounds(x, y)))
		{
			return this.addUnsafe(x, y, component);
		}
		return null;
	}
	
	public LogicEntry addUnsafe(int x, int y, LogicComponent component)
	{
		if(component.type() == LogicTypes.DEBUGGER)
		{
			if(debug)
			{
				return null;
			}
			debug = true;
		}
		int slot = this.pickAvailableSlot();
		var entry = new LogicEntry(slot, x, y, component);
		objects.put(slot, entry);
		return entry;
	}
	
	public List<Wire> remove(int slot)
	{
		List<Wire> wiresRemoved = Lists.newArrayList();
		var original = objects.remove(slot);
		wiresRemoved.addAll(microchip.wires().removeAllOutputs(slot));
		wiresRemoved.addAll(microchip.wires().removeAllInputs(slot));
		if(original.component().type() == LogicTypes.DEBUGGER)
		{
			debug = false;
		}
		return Collections.unmodifiableList(wiresRemoved);
	}
	
	public List<Wire> remove(LogicEntry entry)
	{
		return this.remove(entry.slot());
	}
	
	public void rebuildTraversal()
	{
		traversalOrder = LogicTraversal.buildOrder(microchip);
	}
	
	public void updateValidity()
	{
		for(var entry : objects.values())
		{
			entry.component().config().recalculateValidity(this);
		}
	}
	
	@Override
	public LogicComponents with(Microchip microchip)
	{
		var components = new LogicComponents(microchip);
		components.loadFrom(this);
		return components;
	}
	
	@Override
	public void loadFrom(LogicComponents other)
	{
		this.loadFrom(other, (before) -> new LogicEntry(before.slot(), before.x(), before.y(), before.component()));
	}
	
	public void loadFrom(LogicComponents other, Function<LogicEntry, LogicEntry> conversion)
	{
		Map<Integer, LogicEntry> copiedComponents = Maps.newHashMap();
		other.objects.forEach((slot, entry) -> copiedComponents.put(slot, conversion.apply(entry)));
		objects = copiedComponents;
		debug = other.debug;
	}
	
	@Override
	public void clear()
	{
		super.clear();
		traversalOrder = List.of();
		debug = false;
	}
	
	@Override
	public int hashCode()
	{
		return objects.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicComponents other && this.hashCode() == other.hashCode());
	}
}
