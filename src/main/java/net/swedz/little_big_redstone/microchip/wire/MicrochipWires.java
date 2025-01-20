package net.swedz.little_big_redstone.microchip.wire;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.LogicSelectedPort;
import net.swedz.little_big_redstone.microchip.Microchip;

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
	
	@Override
	public Iterator<Wire> iterator()
	{
		return this.values().iterator();
	}
	
	public List<Wire> getByOutput(int outputSlot)
	{
		var list = wiresByOutputSlot.get(outputSlot);
		return list == null ? List.of() : List.copyOf(list);
	}
	
	public List<Wire> getByInput(int inputSlot)
	{
		var list = wiresByInputSlot.get(inputSlot);
		return list == null ? List.of() : List.copyOf(list);
	}
	
	public boolean add(Wire wire)
	{
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
	
	public boolean add(LogicSelectedPort output, LogicSelectedPort input)
	{
		return this.add(new Wire(output.entry().slot(), output.portIndex(), input.entry().slot(), input.portIndex()));
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
			
			microchip.markDirty();
			
			return true;
		}
		return false;
	}
	
	public void removeAllOutputs(int outputSlot)
	{
		this.getByOutput(outputSlot).forEach(this::remove);
	}
	
	public void removeAllInputs(int inputSlot)
	{
		List<Wire> wiresTargeting = Lists.newArrayList();
		for(Wire wire : wires)
		{
			if(wire.input().slot() == inputSlot)
			{
				wiresTargeting.add(wire);
			}
		}
		wiresTargeting.forEach(this::remove);
	}
	
	public MicrochipWires with(Microchip microchip)
	{
		var wires = new MicrochipWires(microchip);
		wires.loadFrom(this);
		return wires;
	}
	
	public void loadFrom(MicrochipWires other)
	{
		wires = other.wires;
		wiresByOutputSlot = other.wiresByOutputSlot;
		wiresByInputSlot = other.wiresByInputSlot;
	}
	
	public void clear()
	{
		wires.clear();
		wiresByOutputSlot.clear();
		wiresByInputSlot.clear();
	}
}
