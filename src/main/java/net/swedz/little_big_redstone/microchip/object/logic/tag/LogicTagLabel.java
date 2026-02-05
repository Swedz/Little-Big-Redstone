package net.swedz.little_big_redstone.microchip.object.logic.tag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.tesseract.neoforge.api.Assert;

public record LogicTagLabel(
		String label
)
{
	public static final int MAX_LENGTH = 24;
	
	public static final String PATTERN = "[a-zA-Z0-9_/]*";
	
	public static final Codec<LogicTagLabel> CODEC = Codec
			.string(0, MAX_LENGTH)
			.validate((label) ->
			{
				if(!label.matches(PATTERN))
				{
					return DataResult.error(() -> "Tag label does not match pattern " + PATTERN);
				}
				return DataResult.success(label);
			})
			.xmap(LogicTagLabel::new, LogicTagLabel::label);
	
	public static final StreamCodec<ByteBuf, LogicTagLabel> STREAM_CODEC = ByteBufCodecs
			.stringUtf8(MAX_LENGTH)
			.map(LogicTagLabel::new, LogicTagLabel::label);
	
	public static final LogicTagLabel EMPTY = new LogicTagLabel("");
	
	public LogicTagLabel
	{
		Assert.notNull(label);
		Assert.that(label.length() <= MAX_LENGTH, "Logic tag label cannot be > " + MAX_LENGTH);
		Assert.that(label.matches(PATTERN), "Logic tag label does not match pattern " + PATTERN);
	}
}
