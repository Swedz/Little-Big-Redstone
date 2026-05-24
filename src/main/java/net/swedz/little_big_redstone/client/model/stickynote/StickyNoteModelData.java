package net.swedz.little_big_redstone.client.model.stickynote;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.tesseract.api.Assert;

import java.util.Objects;

public final class StickyNoteModelData
{
	public static final ModelProperty<StickyNoteModelData> KEY = new ModelProperty<>();
	
	public static final StickyNoteModelData DEFAULT = new StickyNoteModelData(DyeColor.WHITE, StickyNoteItem.getDefaultTextColor(DyeColor.WHITE), false);
	
	public static StickyNoteModelData get(ModelData modelData)
	{
		var data = modelData.get(KEY);
		return data != null ? data : DEFAULT;
	}
	
	public static StickyNoteModelData of(ItemStack stack)
	{
		if(stack.getItem() instanceof StickyNoteItem stickyNoteItem)
		{
			var note = stack.get(LBRComponents.STICKY_NOTE);
			var color = stickyNoteItem.color();
			var textColor = stack.get(LBRComponents.STICKY_NOTE_TEXT_COLOR);
			return new StickyNoteModelData(color, textColor, !note.isEmpty());
		}
		throw new IllegalArgumentException("Cannot get sticky note model data of non-sticky note item stack");
	}
	
	private final DyeColor color, textColor;
	private final boolean hasText;
	
	public StickyNoteModelData(DyeColor color, DyeColor textColor, boolean hasText)
	{
		Assert.noneNull(color, textColor);
		this.color = color;
		this.textColor = textColor;
		this.hasText = hasText;
	}
	
	public DyeColor color()
	{
		return color;
	}
	
	public DyeColor textColor()
	{
		return textColor;
	}
	
	public boolean hasText()
	{
		return hasText;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(color, textColor, hasText);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof StickyNoteModelData other && Objects.equals(color, other.color) && Objects.equals(textColor, other.textColor) && Objects.equals(hasText, other.hasText));
	}
}
