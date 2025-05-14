package net.swedz.little_big_redstone.microchip.object.note;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.api.Bounds;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.microchip.object.MicrochipObject;
import net.swedz.little_big_redstone.microchip.object.MicrochipObjectContainerType;

import java.util.Objects;
import java.util.Optional;

public final class StickyNoteEntry implements MicrochipObject
{
	public static final Codec<StickyNoteEntry> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.INT.fieldOf("slot").forGetter(StickyNoteEntry::slot),
					Codec.INT.fieldOf("x").forGetter(StickyNoteEntry::x),
					Codec.INT.fieldOf("y").forGetter(StickyNoteEntry::y),
					DyeColor.CODEC.fieldOf("color").forGetter(StickyNoteEntry::noteColor),
					StickyNote.CODEC.fieldOf("note").forGetter(StickyNoteEntry::note),
					DyeColor.CODEC.fieldOf("text_color").forGetter(StickyNoteEntry::textColor)
			)
			.apply(instance, StickyNoteEntry::new));
	
	public static final StreamCodec<ByteBuf, StickyNoteEntry> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, StickyNoteEntry::slot,
			ByteBufCodecs.VAR_INT, StickyNoteEntry::x,
			ByteBufCodecs.VAR_INT, StickyNoteEntry::y,
			DyeColor.STREAM_CODEC, StickyNoteEntry::noteColor,
			StickyNote.STREAM_CODEC, StickyNoteEntry::note,
			DyeColor.STREAM_CODEC, StickyNoteEntry::textColor,
			StickyNoteEntry::new
	);
	
	private final int slot;
	private final int x;
	private final int y;
	private final DyeColor color;
	
	private StickyNote note;
	
	private DyeColor textColor;
	
	public StickyNoteEntry(int slot, int x, int y,
						   DyeColor color, StickyNote note, DyeColor textColor)
	{
		this.slot = slot;
		this.x = x;
		this.y = y;
		this.color = color;
		this.note = note;
		this.textColor = textColor;
	}
	
	@Override
	public int slot()
	{
		return slot;
	}
	
	@Override
	public int x()
	{
		return x;
	}
	
	@Override
	public int y()
	{
		return y;
	}
	
	public DyeColor noteColor()
	{
		return color;
	}
	
	public StickyNote note()
	{
		return note;
	}
	
	public void setNote(StickyNote note)
	{
		this.note = note;
	}
	
	public DyeColor textColor()
	{
		return textColor;
	}
	
	@Override
	public Optional<DyeColor> color()
	{
		return textColor == StickyNoteItem.getDefaultTextColor(color) ? Optional.empty() : Optional.of(textColor);
	}
	
	@Override
	public boolean setColor(Optional<DyeColor> color)
	{
		var original = textColor;
		var resolvedColor = color.orElse(StickyNoteItem.getDefaultTextColor(this.color));
		textColor = resolvedColor;
		return !original.equals(resolvedColor);
	}
	
	@Override
	public MicrochipObjectContainerType containerType()
	{
		return MicrochipObjectContainerType.STICKY_NOTE;
	}
	
	@Override
	public ItemStack toStack()
	{
		var stack = new ItemStack(LBRItems.stickyNote(color));
		stack.set(LBRComponents.STICKY_NOTE, note);
		stack.set(LBRComponents.STICKY_NOTE_TEXT_COLOR, textColor);
		return stack;
	}
	
	@Override
	public Bounds toBounds()
	{
		return new Bounds(x, y, 16, 16);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o == this)
		{
			return true;
		}
		if(o == null || o.getClass() != this.getClass())
		{
			return false;
		}
		var other = (StickyNoteEntry) o;
		return slot == other.slot &&
			   x == other.x &&
			   y == other.y &&
			   Objects.equals(color, other.color) &&
			   Objects.equals(note, other.note) &&
			   Objects.equals(textColor, other.textColor);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(slot, x, y, color, note, textColor);
	}
}
