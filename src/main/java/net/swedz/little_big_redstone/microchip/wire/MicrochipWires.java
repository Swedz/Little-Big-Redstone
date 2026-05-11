package net.swedz.little_big_redstone.microchip.wire;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MicrochipWires implements Iterable<Wire>
{
	public static final Codec<MicrochipWires> CODEC = Codec.list(Wire.CODEC).xmap(MicrochipWires::new, MicrochipWires::values);
	
	public static final StreamCodec<ByteBuf, MicrochipWires> STREAM_CODEC = Wire.STREAM_CODEC.apply(ByteBufCodecs.list()).map(MicrochipWires::new, MicrochipWires::values);
	
	private final Microchip microchip;
	
	private List<Wire>              wires;
	private Map<Integer, Set<Wire>> wiresByOutputSlot;
	private Map<Integer, Set<Wire>> wiresByInputSlot;
	
	/**
	 * Should not ever be used directly, only by other constructors.
	 */
	private MicrochipWires(Microchip microchip, List<Wire> wires)
	{
		this.microchip = microchip;
		this.wires = Lists.newArrayList();
		this.wiresByOutputSlot = Maps.newHashMap();
		this.wiresByInputSlot = Maps.newHashMap();
		wires.forEach(this::add);
	}
	
	/**
	 * Should only be used by codecs. Instances created using this should get converted to one that has a microchip attached (using {@link #with(Microchip)}) before doing anything with it.
	 */
	private MicrochipWires(List<Wire> wires)
	{
		this(null, wires);
	}
	
	public MicrochipWires(Microchip microchip)
	{
		this(microchip, Lists.newArrayList());
	}
	
	public List<Wire> values()
	{
		return Collections.unmodifiableList(wires);
	}
	
	public int size()
	{
		return wires.size();
	}
	
	@Override
	public Iterator<Wire> iterator()
	{
		return this.values().iterator();
	}
	
	public List<Wire> getByOutputSlot(int outputSlot)
	{
		var list = wiresByOutputSlot.get(outputSlot);
		return list == null ? List.of() : List.copyOf(list);
	}
	
	public List<Wire> getByOutputSlot(int outputSlot, int outputPort)
	{
		return this.getByOutputSlot(outputSlot).stream()
				.filter((wire) -> wire.output().index() == outputPort)
				.toList();
	}
	
	public List<Wire> getByOutputSlot(PortReference port)
	{
		return this.getByOutputSlot(port.slot(), port.index());
	}
	
	public List<Wire> getByInputSlot(int inputSlot)
	{
		var list = wiresByInputSlot.get(inputSlot);
		return list == null ? List.of() : List.copyOf(list);
	}
	
	public Wire getByInputSlot(int inputSlot, int inputPort)
	{
		return this.getByInputSlot(inputSlot).stream()
				.filter((wire) -> wire.input().index() == inputPort)
				.findFirst().orElse(null);
	}
	
	public Wire getByInputSlot(PortReference port)
	{
		return this.getByInputSlot(port.slot(), port.index());
	}
	
	public List<Wire> getInvolvingSlot(int slot)
	{
		var outputs = this.getByOutputSlot(slot);
		var inputs = this.getByInputSlot(slot);
		List<Wire> combined = Lists.newArrayList();
		combined.addAll(outputs);
		combined.addAll(inputs);
		return Collections.unmodifiableList(combined);
	}
	
	public Wire get(PortReference output, PortReference input)
	{
		return wires.stream()
				.filter((wire) ->
						wire.output().slot() == output.slot() && wire.output().index() == output.index() &&
						wire.input().slot() == input.slot() && wire.input().index() == input.index())
				.findFirst()
				.orElse(null);
	}
	
	public Wire get(int outputSlot, int outputPort, int inputSlot, int inputPort)
	{
		return this.get(new WirePort(outputSlot, outputPort), new WirePort(inputSlot, inputPort));
	}
	
	public boolean add(Wire wire)
	{
		if(this.getByInputSlot(wire.input()) != null)
		{
			return false;
		}
		if(wiresByOutputSlot.computeIfAbsent(wire.output().slot(), (__) -> Sets.newHashSet()).add(wire))
		{
			wiresByInputSlot.computeIfAbsent(wire.input().slot(), (__) -> Sets.newHashSet()).add(wire);
			wires.add(wire);
			return true;
		}
		return false;
	}
	
	public boolean add(int outputSlot, int outputPort, int inputSlot, int inputPort)
	{
		return this.add(new Wire(outputSlot, outputPort, inputSlot, inputPort));
	}
	
	public boolean add(PortReference output, PortReference input)
	{
		return this.add(new Wire(output.slot(), output.index(), input.slot(), input.index()));
	}
	
	public boolean remove(Wire wire)
	{
		if(wires.remove(wire))
		{
			int outputSlot = wire.output().slot();
			var outputs = wiresByOutputSlot.get(outputSlot);
			outputs.remove(wire);
			if(outputs.isEmpty())
			{
				wiresByOutputSlot.remove(outputSlot);
			}
			
			int inputSlot = wire.input().slot();
			var inputs = wiresByInputSlot.get(inputSlot);
			inputs.remove(wire);
			if(inputs.isEmpty())
			{
				wiresByInputSlot.remove(inputSlot);
			}
			
			return true;
		}
		return false;
	}
	
	public boolean remove(int outputSlot, int outputPort, int inputSlot, int inputPort)
	{
		return this.remove(new Wire(outputSlot, outputPort, inputSlot, inputPort));
	}
	
	public List<Wire> removeAllOutputs(int outputSlot)
	{
		var wires = this.getByOutputSlot(outputSlot);
		wires.forEach(this::remove);
		return wires;
	}
	
	public List<Wire> removeAllInputs(int inputSlot)
	{
		var wires = this.getByInputSlot(inputSlot);
		wires.forEach(this::remove);
		return wires;
	}
	
	public int cleanup(LogicEntry entry)
	{
		int wiresRemoved = 0;
		int slot = entry.slot();
		int totalInputs = entry.component().inputs();
		int totalOutputs = entry.component().outputs();
		for(var wire : this.getInvolvingSlot(slot))
		{
			if((wire.input().slot() == slot && wire.input().index() >= totalInputs) ||
			   (wire.output().slot() == slot && wire.output().index() >= totalOutputs))
			{
				this.remove(wire);
				wiresRemoved++;
			}
		}
		return wiresRemoved;
	}
	
	public MicrochipWires with(Microchip microchip)
	{
		var wires = new MicrochipWires(microchip);
		wires.loadFrom(this);
		return wires;
	}
	
	public void loadFrom(MicrochipWires other)
	{
		wires = Lists.newArrayList(other.wires);
		wiresByOutputSlot = Maps.newHashMap();
		for(var entry : other.wiresByOutputSlot.entrySet())
		{
			wiresByOutputSlot.put(entry.getKey(), Sets.newHashSet(entry.getValue()));
		}
		wiresByInputSlot = Maps.newHashMap();
		for(var entry : other.wiresByInputSlot.entrySet())
		{
			wiresByInputSlot.put(entry.getKey(), Sets.newHashSet(entry.getValue()));
		}
	}
	
	public void clear()
	{
		wires.clear();
		wiresByOutputSlot.clear();
		wiresByInputSlot.clear();
	}
	
	@Override
	public int hashCode()
	{
		return wires.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof MicrochipWires other && wires.equals(other.wires));
	}
}
