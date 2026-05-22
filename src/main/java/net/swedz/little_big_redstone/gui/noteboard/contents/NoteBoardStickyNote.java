package net.swedz.little_big_redstone.gui.noteboard.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.tesseract.neoforge.api.Assert;

public record NoteBoardStickyNote(
		float x,
		float y,
		int size,
		ItemStack stack
)
{
	public static final int DEFAULT_NOTE_SIZE = 90;
	public static final int MIN_NOTE_SIZE     = 45;
	public static final int MAX_NOTE_SIZE     = 180;
	public static final int FULL_NOTE_SIZE    = 180;
	public static final int STEP_NOTE_SIZE    = 45;
	
	public static boolean validateSize(int size)
	{
		return size >= NoteBoardStickyNote.MIN_NOTE_SIZE &&
			   size <= NoteBoardStickyNote.MAX_NOTE_SIZE &&
			   size % NoteBoardStickyNote.STEP_NOTE_SIZE == 0;
	}
	
	public static final Codec<NoteBoardStickyNote> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.floatRange(0, 1).fieldOf("x").forGetter(NoteBoardStickyNote::x),
					Codec.floatRange(0, 1).fieldOf("y").forGetter(NoteBoardStickyNote::y),
					Codec.INT.fieldOf("size").forGetter(NoteBoardStickyNote::size),
					ItemStack.CODEC.fieldOf("stack").forGetter(NoteBoardStickyNote::stack)
			)
			.apply(instance, NoteBoardStickyNote::new));
	
	public static final StreamCodec<RegistryFriendlyByteBuf, NoteBoardStickyNote> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT,
			NoteBoardStickyNote::x,
			ByteBufCodecs.FLOAT,
			NoteBoardStickyNote::y,
			ByteBufCodecs.INT,
			NoteBoardStickyNote::size,
			ItemStack.STREAM_CODEC,
			NoteBoardStickyNote::stack,
			NoteBoardStickyNote::new
	);
	
	public NoteBoardStickyNote
	{
		Assert.notNull(stack);
		Assert.that(stack.getItem() instanceof StickyNoteItem);
		Assert.that(size > 0);
	}
	
	public int x(int width)
	{
		return unscaled(x, width, size);
	}
	
	public int y(int height)
	{
		return unscaled(y, height, size);
	}
	
	public StickyNoteView asView()
	{
		return new StickyNoteView(stack);
	}
	
	public NoteBoardStickyNote withText(String text)
	{
		var newStack = stack.copy();
		newStack.set(LBRComponents.STICKY_NOTE, new StickyNote(text));
		return new NoteBoardStickyNote(x, y, size, newStack);
	}
	
	public NoteBoardStickyNote moveTo(float x, float y, int size)
	{
		return new NoteBoardStickyNote(x, y, size, stack);
	}
	
	public boolean isEditable()
	{
		return stack.get(LBRComponents.STICKY_NOTE_EDITABLE);
	}
	
	public static int unscaled(float coord, int dimension, int size)
	{
		int rawCoord = Math.round(coord * dimension);
		int offset = coord >= 0.5 ? -size : 0;
		return rawCoord + offset;
	}
	
	public static float scaled(int coord, int dimension, int size)
	{
		int middle = Math.round(dimension / 2f);
		int offset = coord >= middle ? -size : 0;
		return (float) (coord - offset) / dimension;
	}
}
