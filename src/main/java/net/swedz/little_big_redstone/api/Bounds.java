package net.swedz.little_big_redstone.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

// TODO move to Tesseract
public record Bounds(int minX, int minY, int width, int height)
{
	public static final Codec<Bounds> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.INT.fieldOf("x").forGetter(Bounds::minX),
					Codec.INT.fieldOf("y").forGetter(Bounds::minY),
					Codec.INT.fieldOf("width").forGetter(Bounds::width),
					Codec.INT.fieldOf("height").forGetter(Bounds::height)
			)
			.apply(instance, Bounds::new));
	
	public static final StreamCodec<ByteBuf, Bounds> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, Bounds::minX,
			ByteBufCodecs.VAR_INT, Bounds::minY,
			ByteBufCodecs.VAR_INT, Bounds::width,
			ByteBufCodecs.VAR_INT, Bounds::height,
			Bounds::new
	);
	
	public int maxX()
	{
		return minX + width - 1;
	}
	
	public int maxY()
	{
		return minY + height - 1;
	}
	
	/**
	 * Checks if the position is within the bounds.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return true if the position is within the bounds, false otherwise
	 */
	public boolean contains(int x, int y)
	{
		return x >= minX && x <= this.maxX() && y >= minY && y <= this.maxY();
	}
	
	/**
	 * Checks if the provided bounds are at all overlapping with this bound.
	 *
	 * @param other the other bounds
	 * @return true if the provided bounds overlaps with this bound
	 */
	public boolean overlaps(Bounds other)
	{
		int minX = other.minX();
		int minY = other.minY();
		int maxX = other.maxX();
		int maxY = other.maxY();
		return this.contains(minX, minY) || this.contains(maxX, minY) ||
			   this.contains(minX, maxY) || this.contains(maxX, maxY);
	}
	
	/**
	 * Checks if the provided bounds is entirely within this bound.
	 *
	 * @param other the other bounds
	 * @return true if the provided bounds is entirely contained within this bound
	 */
	public boolean contains(Bounds other)
	{
		int minX = other.minX();
		int minY = other.minY();
		int maxX = other.maxX();
		int maxY = other.maxY();
		return this.contains(minX, minY) && this.contains(maxX, minY) &&
			   this.contains(minX, maxY) && this.contains(maxX, maxY);
	}
	
	/**
	 * Creates a new bounds instance with the origin x and y at 0, 0.
	 *
	 * @return the normalized bounds
	 */
	public Bounds normalize()
	{
		return new Bounds(0, 0, width, height);
	}
}
