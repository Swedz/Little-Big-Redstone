package net.swedz.little_big_redstone.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.swedz.tesseract.neoforge.api.Assert;

// TODO move to Tesseract
public record IntRange(int min, int max)
{
	public static final Codec<IntRange> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.INT.fieldOf("min").forGetter(IntRange::min),
					Codec.INT.fieldOf("max").forGetter(IntRange::max)
			)
			.apply(instance, IntRange::new));
	
	public static final StreamCodec<ByteBuf, IntRange> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, IntRange::min,
			ByteBufCodecs.VAR_INT, IntRange::max,
			IntRange::new
	);
	
	public IntRange
	{
		Assert.that(min <= max, "Cannot create int range with min that is greater than the max (%d > %d)".formatted(min, max));
	}
	
	public boolean isSingle()
	{
		return min == max;
	}
	
	public boolean contains(int value)
	{
		return value >= min && value <= max;
	}
	
	public int clamp(int value)
	{
		return Mth.clamp(value, min, max);
	}
}
