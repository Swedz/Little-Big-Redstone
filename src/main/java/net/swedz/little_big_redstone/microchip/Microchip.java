package net.swedz.little_big_redstone.microchip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.api.Bounds;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAwarenesses;
import net.swedz.little_big_redstone.microchip.object.MicrochipObject;
import net.swedz.little_big_redstone.microchip.object.MicrochipObjectContainer;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponents;
import net.swedz.little_big_redstone.microchip.object.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.wire.MicrochipWires;

import java.util.List;
import java.util.Objects;

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
	
	private final MicrochipAwarenesses awarenesses;
	
	private boolean dirty;
	
	private Microchip(MicrochipSize size, LogicComponents components, MicrochipWires wires)
	{
		this.size = size;
		this.components = components.with(this);
		this.components.updateValidity();
		this.wires = wires.with(this);
		this.components.rebuildTraversal();
		this.awarenesses = new MicrochipAwarenesses();
		this.awarenesses.rebuild(this);
		this.awarenesses.load(this);
	}
	
	public Microchip(MicrochipSize size)
	{
		this.size = size;
		this.components = new LogicComponents(this);
		this.components.updateValidity();
		this.wires = new MicrochipWires(this);
		this.components.rebuildTraversal();
		this.awarenesses = new MicrochipAwarenesses();
		this.awarenesses.rebuild(this);
	}
	
	public MicrochipSize size()
	{
		return size;
	}
	
	public LogicComponents components()
	{
		return components;
	}
	
	public boolean isDebug()
	{
		return components.isDebug();
	}
	
	public MicrochipWires wires()
	{
		return wires;
	}
	
	public MicrochipAwarenesses awarenesses()
	{
		return awarenesses;
	}
	
	private List<MicrochipObjectContainer<?, ?>> objectContainers()
	{
		return List.of(components);
	}
	
	public boolean canFit(Bounds bounds)
	{
		for(var container : this.objectContainers())
		{
			if(!container.canFit(bounds))
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean canFit(int x, int y, LogicComponent component)
	{
		return this.canFit(component.size().toBounds(x, y));
	}
	
	public MicrochipObject findAt(int x, int y)
	{
		for(var container : this.objectContainers())
		{
			var found = container.findAt(x, y);
			if(found != null)
			{
				return found;
			}
		}
		return null;
	}
	
	public void loadFrom(Microchip other)
	{
		components.loadFrom(other.components());
		wires.loadFrom(other.wires());
		this.markDirty();
	}
	
	public void loadFrom(Immutable other)
	{
		components.loadFrom(other.components);
		wires.loadFrom(other.wires);
		this.markDirty();
	}
	
	public Immutable immutable()
	{
		return new Immutable(this);
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
		awarenesses.rebuild(this);
		awarenesses.load(this);
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
	
	@Override
	public int hashCode()
	{
		return Objects.hash(components, wires);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof Immutable other && components.equals(other.components) && wires.equals(other.wires));
	}
	
	/**
	 * <p>An immutable copy of a {@link Microchip}. Used for storing a program in a
	 * {@link net.swedz.little_big_redstone.item.FloppyDiskItem}.</p>
	 */
	public static final class Immutable
	{
		public static final Codec<Immutable> CODEC = RecordCodecBuilder.create((instance) -> instance
				.group(
						LogicComponents.CODEC.fieldOf("components").forGetter((m) -> m.components),
						MicrochipWires.CODEC.fieldOf("wires").forGetter((m) -> m.wires)
				)
				.apply(instance, Immutable::new));
		
		public static final StreamCodec<ByteBuf, Immutable> STREAM_CODEC = StreamCodec.composite(
				LogicComponents.STREAM_CODEC, (m) -> m.components,
				MicrochipWires.STREAM_CODEC, (m) -> m.wires,
				Immutable::new
		);
		
		private final LogicComponents components;
		private final MicrochipWires  wires;
		
		/**
		 * Should only be used by codecs.
		 */
		private Immutable(LogicComponents components, MicrochipWires wires)
		{
			this.components = components;
			this.wires = wires;
		}
		
		/**
		 * Creates an immutable deep copy of a {@link Microchip}.
		 *
		 * @param microchip the {@link Microchip} to copy
		 */
		private Immutable(Microchip microchip)
		{
			Microchip copy = new Microchip(microchip.size());
			components = new LogicComponents(copy);
			components.loadFrom(microchip.components);
			wires = new MicrochipWires(copy);
			wires.loadFrom(microchip.wires);
		}
		
		/**
		 * <p>Gets an iterable instance of the components on this immutable {@link Microchip}.</p>
		 *
		 * <p><b>IMPORTANT:</b> The entries yielded by this iterable contain logic components that <i>MUST NOT</i> be
		 * modified. Since this object is a data component, we must respect the promise that data components are
		 * immutable, but logic components cannot be immutable.</p>
		 *
		 * @return the iterable instance
		 */
		public Iterable<LogicEntry> components()
		{
			return components;
		}
		
		public int wireCount()
		{
			return wires.values().size();
		}
		
		@Override
		public int hashCode()
		{
			return Objects.hash(components, wires);
		}
		
		@Override
		public boolean equals(Object o)
		{
			return this == o ||
				   (o instanceof Immutable other && components.equals(other.components) && wires.equals(other.wires));
		}
	}
}
