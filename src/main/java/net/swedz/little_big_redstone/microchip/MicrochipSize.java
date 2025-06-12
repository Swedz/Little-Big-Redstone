package net.swedz.little_big_redstone.microchip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.tesseract.neoforge.api.Bounds;

public final class MicrochipSize
{
	public static final Codec<MicrochipSize> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Bounds.CODEC.fieldOf("bounds").forGetter(MicrochipSize::bounds),
					Codec.FLOAT.fieldOf("scale").forGetter(MicrochipSize::scale)
			)
			.apply(instance, MicrochipSize::new));
	
	public static final StreamCodec<ByteBuf, MicrochipSize> STREAM_CODEC = StreamCodec.composite(
			Bounds.STREAM_CODEC, MicrochipSize::bounds,
			ByteBufCodecs.FLOAT, MicrochipSize::scale,
			MicrochipSize::new
	);
	
	private static final Bounds REAL_BOUNDS = new Bounds(0, 0, 240, 128);
	
	public static MicrochipSize create(Bounds bounds, float scale)
	{
		return new MicrochipSize(bounds.divideCeil(scale), scale);
	}
	
	public static MicrochipSize create(float scale)
	{
		return create(REAL_BOUNDS, scale);
	}
	
	private final Bounds bounds;
	private final float  scale;
	
	private MicrochipSize(Bounds bounds, float scale)
	{
		this.bounds = bounds;
		this.scale = scale;
	}
	
	public Bounds bounds()
	{
		return bounds;
	}
	
	public float scale()
	{
		return scale;
	}
	
	public int boardCoord(int coord)
	{
		return Math.round(coord / scale);
	}
	
	public int scale(int coord)
	{
		return Math.round(coord * scale);
	}
}
