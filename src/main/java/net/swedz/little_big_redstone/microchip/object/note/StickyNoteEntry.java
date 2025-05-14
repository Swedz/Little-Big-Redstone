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
import net.swedz.little_big_redstone.microchip.object.MicrochipObject;

public record StickyNoteEntry(
		int slot, int x, int y,
		DyeColor color, StickyNote note, DyeColor textColor
) implements MicrochipObject
{
	public static final Codec<StickyNoteEntry> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.INT.fieldOf("slot").forGetter(StickyNoteEntry::slot),
					Codec.INT.fieldOf("x").forGetter(StickyNoteEntry::x),
					Codec.INT.fieldOf("y").forGetter(StickyNoteEntry::y),
					DyeColor.CODEC.fieldOf("color").forGetter(StickyNoteEntry::color),
					StickyNote.CODEC.fieldOf("note").forGetter(StickyNoteEntry::note),
					DyeColor.CODEC.fieldOf("text_color").forGetter(StickyNoteEntry::textColor)
			)
			.apply(instance, StickyNoteEntry::new));
	
	public static final StreamCodec<ByteBuf, StickyNoteEntry> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, StickyNoteEntry::slot,
			ByteBufCodecs.VAR_INT, StickyNoteEntry::x,
			ByteBufCodecs.VAR_INT, StickyNoteEntry::y,
			DyeColor.STREAM_CODEC, StickyNoteEntry::color,
			StickyNote.STREAM_CODEC, StickyNoteEntry::note,
			DyeColor.STREAM_CODEC, StickyNoteEntry::textColor,
			StickyNoteEntry::new
	);
	
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
}
