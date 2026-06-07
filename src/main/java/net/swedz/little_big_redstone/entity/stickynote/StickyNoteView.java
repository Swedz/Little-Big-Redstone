package net.swedz.little_big_redstone.entity.stickynote;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRComponents;

public record StickyNoteView(
		DyeColor color,
		DyeColor textColor,
		Component text
)
{
	public static final StreamCodec<RegistryFriendlyByteBuf, StickyNoteView> STREAM_CODEC = StreamCodec.composite(
			DyeColor.STREAM_CODEC, StickyNoteView::color,
			DyeColor.STREAM_CODEC, StickyNoteView::textColor,
			ComponentSerialization.STREAM_CODEC, StickyNoteView::text,
			StickyNoteView::new
	);
	
	public StickyNoteView(ItemStack stack)
	{
		this(
				stack.get(LBRComponents.STICKY_NOTE_COLOR),
				stack.get(LBRComponents.STICKY_NOTE_TEXT_COLOR),
				stack.get(LBRComponents.STICKY_NOTE).parsed()
		);
	}
	
	public StickyNoteView(StickyNoteEntity entity)
	{
		this(
				entity.getColor(),
				entity.getTextColor(),
				entity.getNote().parsed()
		);
	}
}
