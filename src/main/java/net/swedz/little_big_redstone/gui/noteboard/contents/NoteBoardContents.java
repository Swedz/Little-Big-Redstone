package net.swedz.little_big_redstone.gui.noteboard.contents;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.tesseract.api.Assert;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class NoteBoardContents implements Iterable<NoteBoardStickyNote>
{
	public static final Codec<NoteBoardContents> CODEC = NoteBoardStickyNote.CODEC
			.listOf()
			.xmap(
					NoteBoardContents::new,
					(board) -> board.contents
			);
	
	public static final StreamCodec<RegistryFriendlyByteBuf, NoteBoardContents> STREAM_CODEC = NoteBoardStickyNote.STREAM_CODEC
			.apply(ByteBufCodecs.list())
			.map(
					NoteBoardContents::new,
					(board) -> board.contents
			);
	
	public static final NoteBoardContents EMPTY = new NoteBoardContents(List.of());
	
	private final List<NoteBoardStickyNote> contents;
	
	private NoteBoardContents(List<NoteBoardStickyNote> contents)
	{
		this.contents = Collections.unmodifiableList(contents);
	}
	
	public boolean isEmpty()
	{
		return contents.isEmpty();
	}
	
	public int size()
	{
		return contents.size();
	}
	
	public boolean has(int index)
	{
		return index >= 0 && index < this.size();
	}
	
	@Override
	public Iterator<NoteBoardStickyNote> iterator()
	{
		return contents.iterator();
	}
	
	public NoteBoardContents update(int index, NoteBoardStickyNote note)
	{
		Assert.notNull(note);
		Assert.that(this.has(index), ArrayIndexOutOfBoundsException::new);
		
		List<NoteBoardStickyNote> contents = Lists.newArrayList(this.contents);
		contents.set(index, note);
		return new NoteBoardContents(contents);
	}
	
	public NoteBoardContents remove(int index)
	{
		Assert.that(this.has(index), ArrayIndexOutOfBoundsException::new);
		
		List<NoteBoardStickyNote> contents = Lists.newArrayList(this.contents);
		contents.remove(index);
		return contents.isEmpty() ? EMPTY : new NoteBoardContents(contents);
	}
	
	public NoteBoardContents add(NoteBoardStickyNote note)
	{
		Assert.notNull(note);
		
		List<NoteBoardStickyNote> contents = Lists.newArrayList(this.contents);
		contents.add(note);
		return new NoteBoardContents(contents);
	}
	
	public NoteBoardStickyNote get(int index)
	{
		return contents.get(index);
	}
	
	public int findAt(int x, int y, int guiWidth, int guiHeight)
	{
		for(int index = this.size() - 1; index >= 0; index--)
		{
			var note = this.get(index);
			int noteX = note.x(guiWidth);
			int noteY = note.y(guiHeight);
			if(x >= noteX && y >= noteY &&
			   x <= noteX + note.size() && y <= noteY + note.size())
			{
				return index;
			}
		}
		return -1;
	}
	
	@Override
	public int hashCode()
	{
		return contents.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof NoteBoardContents other && contents.equals(other.contents);
	}
}
