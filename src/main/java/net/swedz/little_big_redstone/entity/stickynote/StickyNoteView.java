package net.swedz.little_big_redstone.entity.stickynote;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;

public record StickyNoteView(DyeColor color, DyeColor textColor, Component text)
{
	public static final StreamCodec<RegistryFriendlyByteBuf, StickyNoteView> STREAM_CODEC = StreamCodec.composite(
			DyeColor.STREAM_CODEC, StickyNoteView::color,
			DyeColor.STREAM_CODEC, StickyNoteView::textColor,
			ComponentSerialization.STREAM_CODEC, StickyNoteView::text,
			StickyNoteView::new
	);
}
