package net.swedz.little_big_redstone.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.swedz.tesseract.neoforge.api.Assert;

// TODO move to Tesseract
public record FloatRange(float min, float max)
{
	public static final Codec<FloatRange> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.FLOAT.fieldOf("min").forGetter(FloatRange::min),
					Codec.FLOAT.fieldOf("max").forGetter(FloatRange::max)
			)
			.apply(instance, FloatRange::new));
	
	public static final StreamCodec<ByteBuf, FloatRange> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT, FloatRange::min,
			ByteBufCodecs.FLOAT, FloatRange::max,
			FloatRange::new
	);
	
	public FloatRange
	{
		Assert.that(min <= max, "Cannot create float range with min that is greater than the max (%f > %f)".formatted(min, max));
	}
	
	public boolean isSingle()
	{
		return min == max;
	}
	
	public boolean contains(float value)
	{
		return value >= min && value <= max;
	}
	
	public float clamp(float value)
	{
		return Mth.clamp(value, min, max);
	}
}
