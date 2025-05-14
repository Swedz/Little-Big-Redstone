package net.swedz.little_big_redstone.microchip.object.note;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.object.MicrochipObjectContainer;

import java.util.List;

public final class MicrochipStickyNotes extends MicrochipObjectContainer<StickyNoteEntry, MicrochipStickyNotes>
{
	public static final Codec<MicrochipStickyNotes> CODEC = Codec.list(StickyNoteEntry.CODEC).xmap(MicrochipStickyNotes::new, MicrochipStickyNotes::values);
	
	public static final StreamCodec<ByteBuf, MicrochipStickyNotes> STREAM_CODEC = StickyNoteEntry.STREAM_CODEC.apply(ByteBufCodecs.list()).map(MicrochipStickyNotes::new, MicrochipStickyNotes::values);
	
	/**
	 * Should not ever be used directly, only by other constructors.
	 */
	private MicrochipStickyNotes(Microchip microchip, List<StickyNoteEntry> notes)
	{
		super(microchip, notes);
	}
	
	/**
	 * Should only be used by codecs. Instances created using this should get converted to one that has a microchip attached (using {@link #with(Microchip)}) before doing anything with it.
	 */
	private MicrochipStickyNotes(List<StickyNoteEntry> notes)
	{
		this(null, notes);
	}
	
	public MicrochipStickyNotes(Microchip microchip)
	{
		this(microchip, Lists.newArrayList());
	}
	
	private StickyNoteEntry add(int x, int y, DyeColor color, StickyNote note, DyeColor textColor)
	{
		int slot = this.pickAvailableSlot();
		var entry = new StickyNoteEntry(slot, x, y, color, note, textColor);
		if(microchip.canFit(entry.toBounds()))
		{
			objects.put(slot, entry);
			return entry;
		}
		return null;
	}
	
	public StickyNoteEntry add(int x, int y, ItemStack stack)
	{
		if(stack.getItem() instanceof StickyNoteItem stickyNote)
		{
			var color = stickyNote.color();
			var note = stack.get(LBRComponents.STICKY_NOTE);
			var textColor = stack.get(LBRComponents.STICKY_NOTE_TEXT_COLOR);
			return this.add(x, y, color, note, textColor);
		}
		return null;
	}
	
	public void remove(int slot)
	{
		objects.remove(slot);
	}
	
	public void remove(StickyNoteEntry entry)
	{
		this.remove(entry.slot());
	}
	
	@Override
	public MicrochipStickyNotes with(Microchip microchip)
	{
		var notes = new MicrochipStickyNotes(microchip);
		notes.loadFrom(this);
		return notes;
	}
	
	@Override
	public void loadFrom(MicrochipStickyNotes other)
	{
		objects = Maps.newHashMap(other.objects);
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
			   (o instanceof MicrochipStickyNotes other && this.hashCode() == other.hashCode());
	}
}
