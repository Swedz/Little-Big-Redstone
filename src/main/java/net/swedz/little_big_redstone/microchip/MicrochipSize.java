package net.swedz.little_big_redstone.microchip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.api.Bounds;

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
	
	private static final Bounds REAL_BOUNDS = new Bounds(8, 20, 240, 133);
	
	public static MicrochipSize create(float scale)
	{
		return new MicrochipSize(REAL_BOUNDS.divideCeil(scale), scale);
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
	
	public int boardX(int x)
	{
		return (int) (x / scale);
	}
	
	public int boardY(int y)
	{
		return (int) (y / scale);
	}
	
	public int scale(int coord)
	{
		return (int) (coord * scale);
	}
}
