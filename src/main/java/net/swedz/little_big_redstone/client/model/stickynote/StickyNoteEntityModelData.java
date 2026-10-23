package net.swedz.little_big_redstone.client.model.stickynote;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;

public record StickyNoteEntityModelData(
		DyeColor paperColor,
		boolean showText,
		DyeColor textColor
)
{
	public static final Codec<StickyNoteEntityModelData> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					DyeColor.CODEC.fieldOf("paper_color").forGetter(StickyNoteEntityModelData::paperColor),
					Codec.BOOL.fieldOf("show_text").forGetter(StickyNoteEntityModelData::showText),
					DyeColor.CODEC.fieldOf("text_color").forGetter(StickyNoteEntityModelData::textColor)
			)
			.apply(instance, StickyNoteEntityModelData::new));
	
	public static final StreamCodec<ByteBuf, StickyNoteEntityModelData> STREAM_CODEC = StreamCodec.composite(
			DyeColor.STREAM_CODEC,
			StickyNoteEntityModelData::paperColor,
			ByteBufCodecs.BOOL,
			StickyNoteEntityModelData::showText,
			DyeColor.STREAM_CODEC,
			StickyNoteEntityModelData::textColor,
			StickyNoteEntityModelData::new
	);
}
