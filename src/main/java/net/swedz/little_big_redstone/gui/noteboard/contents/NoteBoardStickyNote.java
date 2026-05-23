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
		return toPixelCoord(x, width, size);
	}
	
	public int y(int height)
	{
		return toPixelCoord(y, height, size);
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
	
	/**
	 * Converts a percentage coordinate value to a literal pixel coordinate.
	 *
	 * @param coord the percentage coordinate
	 * @param dimension the dimension's length (width or height) in pixels
	 * @param size the size of the note in pixels
	 * @return the literal pixel coordinate, the top left of the note
	 */
	public static int toPixelCoord(float coord, int dimension, int size)
	{
		int rawCoord = Math.round(coord * dimension);
		int offset = coord >= 0.6 ? -size : (coord >= 0.4 ? -(size / 2) : 0);
		return rawCoord + offset;
	}
	
	/**
	 * Converts a literal pixel coordinate to a percentage coordinate.
	 *
	 * @param coord the literal pixel coordinate, the top left of the note
	 * @param dimension the dimension's length (width or height) in pixels
	 * @param size the size of the note in pixels
	 * @return the percentage coordinate
	 */
	public static float toPercentageCoord(int coord, int dimension, int size)
	{
		int end = Math.round(dimension * 0.6f);
		int middle = Math.round(dimension * 0.4f);
		int centerCoord = coord + (size / 2);
		int offset = centerCoord >= end ? -size : (centerCoord >= middle ? -(size / 2) : 0);
		return (float) (coord - offset) / dimension;
	}
}
