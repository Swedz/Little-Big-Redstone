package net.swedz.little_big_redstone.microchip;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.item.FloppyDiskItem;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAwarenesses;
import net.swedz.little_big_redstone.microchip.object.MicrochipObject;
import net.swedz.little_big_redstone.microchip.object.MicrochipObjectContainer;
import net.swedz.little_big_redstone.microchip.object.MicrochipObjectContainerType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponents;
import net.swedz.little_big_redstone.microchip.object.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;
import net.swedz.little_big_redstone.microchip.object.note.MicrochipStickyNotes;
import net.swedz.little_big_redstone.microchip.object.note.StickyNoteEntry;
import net.swedz.little_big_redstone.microchip.wire.MicrochipWires;
import net.swedz.tesseract.neoforge.api.Bounds;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Microchip
{
	public static final Codec<Microchip> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					MicrochipSize.CODEC.fieldOf("size").forGetter(Microchip::size),
					MicrochipStickyNotes.CODEC.optionalFieldOf("sticky_notes").forGetter((m) -> Optional.of(m.stickyNotes())),
					LogicComponents.CODEC.optionalFieldOf("components").forGetter((m) -> Optional.of(m.components())),
					MicrochipWires.CODEC.optionalFieldOf("wires").forGetter((m) -> Optional.of(m.wires()))
			)
			.apply(instance, (size, notes, components, wires) -> new Microchip(size, notes.orElse(null), components.orElse(null), wires.orElse(null))));
	
	public static final StreamCodec<ByteBuf, Microchip> STREAM_CODEC = StreamCodec.composite(
			MicrochipSize.STREAM_CODEC, Microchip::size,
			MicrochipStickyNotes.STREAM_CODEC, Microchip::stickyNotes,
			LogicComponents.STREAM_CODEC, Microchip::components,
			MicrochipWires.STREAM_CODEC, Microchip::wires,
			Microchip::new
	);
	
	private final MicrochipSize size;
	
	private final MicrochipStickyNotes stickyNotes;
	private final LogicComponents      components;
	private final MicrochipWires       wires;
	
	private final MicrochipAwarenesses awarenesses;
	
	private boolean dirty;
	
	private Microchip(MicrochipSize size, MicrochipStickyNotes stickyNotes, LogicComponents components, MicrochipWires wires)
	{
		this.size = size;
		this.stickyNotes = stickyNotes != null ? stickyNotes.with(this) : new MicrochipStickyNotes(this);
		this.components = components != null ? components.with(this) : new LogicComponents(this);
		this.components.updateValidity();
		this.wires = wires != null ? wires.with(this) : new MicrochipWires(this);
		this.components.rebuildTraversal();
		this.awarenesses = new MicrochipAwarenesses();
		this.awarenesses.rebuild(this);
		this.awarenesses.load(this);
		removeDanglingWires(this.wires, this.components);
	}
	
	public Microchip(MicrochipSize size)
	{
		this.size = size;
		this.stickyNotes = new MicrochipStickyNotes(this);
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
	
	public MicrochipStickyNotes stickyNotes()
	{
		return stickyNotes;
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
		return List.of(stickyNotes, components);
	}
	
	public Iterable<MicrochipObject> objects()
	{
		return Iterables.concat(this.objectContainers());
	}
	
	private MicrochipObjectContainer<?, ?> getContainer(MicrochipObjectContainerType containerType)
	{
		return switch (containerType)
		{
			case STICKY_NOTE -> stickyNotes;
			case LOGIC_COMPONENT -> components;
		};
	}
	
	public MicrochipObject get(int slot, MicrochipObjectContainerType containerType)
	{
		return this.getContainer(containerType).get(slot);
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
	
	public MicrochipObject findAt(int x, int y, int padding)
	{
		for(var container : this.objectContainers())
		{
			var found = container.findAt(x, y, padding);
			if(found != null)
			{
				return found;
			}
		}
		return null;
	}
	
	public MicrochipObject findAt(int x, int y)
	{
		return this.findAt(x, y, 0);
	}
	
	public MicrochipObject findAt(int x, int y, int padding, MicrochipObjectContainerType containerType)
	{
		return this.getContainer(containerType).findAt(x, y, padding);
	}
	
	public MicrochipObject findAt(int x, int y, MicrochipObjectContainerType containerType)
	{
		return this.findAt(x, y, 0, containerType);
	}
	
	public void loadFrom(Microchip other)
	{
		stickyNotes.loadFrom(other.stickyNotes());
		components.loadFrom(other.components());
		wires.loadFrom(other.wires());
		this.markDirty();
	}
	
	public void loadFrom(Immutable other)
	{
		stickyNotes.loadFrom(other.stickyNotes);
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
		stickyNotes.clear();
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
		return Objects.hash(stickyNotes, components, wires);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof Immutable other && stickyNotes.equals(other.stickyNotes) && components.equals(other.components) && wires.equals(other.wires));
	}
	
	/**
	 * Removes all wires with no valid connections. This is used to clear out invalid wires when loading Microchips.
	 * Helps prevent tampered program files creating broken states when loaded into a {@link FloppyDiskItem}.
	 *
	 * @param wires      the wires of the microchip
	 * @param components the components of the microchip
	 * @return true if any dangling wires were removed, false otherwise
	 */
	private static boolean removeDanglingWires(MicrochipWires wires, LogicComponents components)
	{
		boolean removed = false;
		for(var wire : Lists.newArrayList(wires))
		{
			if(!components.has(wire.output().slot()) ||
			   !components.has(wire.input().slot()))
			{
				wires.remove(wire);
				removed = true;
			}
		}
		return removed;
	}
	
	/**
	 * <p>An immutable copy of a {@link Microchip}. Used for storing a program in a
	 * {@link FloppyDiskItem}.</p>
	 */
	public static final class Immutable
	{
		public static final Codec<Immutable> CODEC = RecordCodecBuilder.create((instance) -> instance
				.group(
						MicrochipStickyNotes.CODEC.optionalFieldOf("sticky_notes").forGetter((m) -> Optional.of(m.stickyNotes)),
						LogicComponents.CODEC.optionalFieldOf("components").forGetter((m) -> Optional.of(m.components)),
						MicrochipWires.CODEC.optionalFieldOf("wires").forGetter((m) -> Optional.of(m.wires))
				)
				.apply(instance, (notes, components, wires) -> new Immutable(notes.orElse(null), components.orElse(null), wires.orElse(null))));
		
		public static final StreamCodec<ByteBuf, Immutable> STREAM_CODEC = StreamCodec.composite(
				MicrochipStickyNotes.STREAM_CODEC, (m) -> m.stickyNotes,
				LogicComponents.STREAM_CODEC, (m) -> m.components,
				MicrochipWires.STREAM_CODEC, (m) -> m.wires,
				Immutable::new
		);
		
		private final MicrochipStickyNotes stickyNotes;
		private final LogicComponents      components;
		private final MicrochipWires       wires;
		
		/**
		 * Should only be used by codecs.
		 */
		private Immutable(MicrochipStickyNotes stickyNotes, LogicComponents components, MicrochipWires wires)
		{
			this.stickyNotes = stickyNotes != null ? stickyNotes : new MicrochipStickyNotes(null);
			this.components = components != null ? components : new LogicComponents(null);
			this.wires = wires != null ? wires : new MicrochipWires(null);
			removeDanglingWires(this.wires, this.components);
		}
		
		/**
		 * Creates an immutable deep copy of a {@link Microchip}.
		 *
		 * @param microchip the {@link Microchip} to copy
		 */
		private Immutable(Microchip microchip)
		{
			Microchip copy = new Microchip(microchip.size());
			stickyNotes = new MicrochipStickyNotes(copy);
			stickyNotes.loadFrom(microchip.stickyNotes);
			components = new LogicComponents(copy);
			components.loadFrom(microchip.components);
			wires = new MicrochipWires(copy);
			wires.loadFrom(microchip.wires);
		}
		
		/**
		 * Gets an iterable instance of the sticky notes on this immutable {@link Microchip}.
		 *
		 * @return the iterable instance
		 */
		public Iterable<StickyNoteEntry> stickyNotes()
		{
			return stickyNotes;
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
			return Objects.hash(stickyNotes, components, wires);
		}
		
		@Override
		public boolean equals(Object o)
		{
			return this == o ||
				   (o instanceof Immutable other && stickyNotes.equals(other.stickyNotes) && components.equals(other.components) && wires.equals(other.wires));
		}
	}
}
