package net.swedz.little_big_redstone.client.model.stickynote;

import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;
import net.swedz.tesseract.neoforge.api.Assert;

import java.util.Objects;

public final class StickyNoteModelData
{
	public static final ModelProperty<StickyNoteModelData> KEY = new ModelProperty<>();
	
	public static final StickyNoteModelData DEFAULT = new StickyNoteModelData(DyeColor.WHITE, StickyNoteEntity.getDefaultTextColor(DyeColor.WHITE));
	
	public static StickyNoteModelData get(ModelData modelData)
	{
		var data = modelData.get(StickyNoteModelData.KEY);
		return data != null ? data : StickyNoteModelData.DEFAULT;
	}
	
	private final DyeColor color, textColor;
	
	public StickyNoteModelData(DyeColor color, DyeColor textColor)
	{
		Assert.noneNull(color, textColor);
		this.color = color;
		this.textColor = textColor;
	}
	
	public DyeColor color()
	{
		return color;
	}
	
	public DyeColor textColor()
	{
		return textColor;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(color, textColor);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof StickyNoteModelData other && Objects.equals(color, other.color) && Objects.equals(textColor, other.textColor));
	}
}
