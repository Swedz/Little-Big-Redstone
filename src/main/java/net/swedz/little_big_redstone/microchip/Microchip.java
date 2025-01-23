package net.swedz.little_big_redstone.microchip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.logic.LogicComponents;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.wire.MicrochipWires;

public final class Microchip
{
	public static final Codec<Microchip> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					MicrochipSize.CODEC.fieldOf("size").forGetter(Microchip::size),
					LogicComponents.CODEC.fieldOf("components").forGetter(Microchip::components),
					MicrochipWires.CODEC.fieldOf("wires").forGetter(Microchip::wires)
			)
			.apply(instance, Microchip::new));
	
	public static final StreamCodec<ByteBuf, Microchip> STREAM_CODEC = StreamCodec.composite(
			MicrochipSize.STREAM_CODEC, Microchip::size,
			LogicComponents.STREAM_CODEC, Microchip::components,
			MicrochipWires.STREAM_CODEC, Microchip::wires,
			Microchip::new
	);
	
	private final MicrochipSize size;
	
	private final LogicComponents components;
	private final MicrochipWires  wires;
	
	private final MicrochipRedstoneIOCache redstoneIOCache;
	
	private boolean dirty;
	
	private Microchip(MicrochipSize size, LogicComponents components, MicrochipWires wires)
	{
		this.size = size;
		this.components = components.with(this);
		this.wires = wires.with(this);
		this.components.rebuildTraversal();
		this.redstoneIOCache = new MicrochipRedstoneIOCache(this);
		this.redstoneIOCache.rebuild();
	}
	
	public Microchip(MicrochipSize size)
	{
		this.size = size;
		this.components = new LogicComponents(this);
		this.wires = new MicrochipWires(this);
		this.components.rebuildTraversal();
		this.redstoneIOCache = new MicrochipRedstoneIOCache(this);
	}
	
	public MicrochipSize size()
	{
		return size;
	}
	
	public LogicComponents components()
	{
		return components;
	}
	
	public MicrochipWires wires()
	{
		return wires;
	}
	
	public MicrochipRedstoneIOCache redstoneIOCache()
	{
		return redstoneIOCache;
	}
	
	public void loadFrom(Microchip other)
	{
		components.loadFrom(other.components());
		wires.loadFrom(other.wires());
		this.markDirty();
	}
	
	public void clear()
	{
		components.clear();
		wires.clear();
		this.markDirty();
	}
	
	public boolean isDirty()
	{
		return dirty;
	}
	
	public void markDirty()
	{
		components.rebuildTraversal();
		redstoneIOCache.rebuild();
		dirty = true;
	}
	
	public void markClean()
	{
		dirty = false;
	}
	
	public void tickLogic(LogicContext context)
	{
		for(var entry : components.traversal())
		{
			int inputSlot = entry.slot();
			int totalInputs = entry.component().inputs();
			boolean[] inputs = new boolean[totalInputs];
			outer:
			for(int inputPort = 0; inputPort < totalInputs; inputPort++)
			{
				for(var wire : wires.getByInputSlot(inputSlot))
				{
					if(wire.input().index() == inputPort)
					{
						if(components.get(wire.output().slot()).component().output(wire.output().index()))
						{
							inputs[inputPort] = true;
							continue outer;
						}
					}
				}
			}
			entry.component().processTick(context, inputs);
		}
	}
}
