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
	 * Gets the x coordinate relative to the origin x of this bounds.
	 *
	 * @param x the x coordinate to compare to
	 * @return the relative x coordinate
	 */
	public int relativeX(int x)
	{
		return x - minX;
	}
	
	/**
	 * Gets the y coordinate relative to the origin y of this bounds.
	 *
	 * @param y the y coordinate to compare to
	 * @return the relative y coordinate
	 */
	public int relativeY(int y)
	{
		return y - minY;
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
	
	/**
	 * Creates a new bounds instance with the origin x and y of this bounds plus the input x and y.
	 *
	 * @param x the x to move the bounds by
	 * @param y the y to move the bounds by
	 * @return the moved bounds
	 */
	public Bounds move(int x, int y)
	{
		return new Bounds(minX + x, minY + y, width, height);
	}
	
	/**
	 * Creates a new bounds instance with the origin x and y of this bounds but with a new width and height.
	 *
	 * @param width  the new width
	 * @param height the new height
	 * @return the resized bounds
	 */
	public Bounds resize(int width, int height)
	{
		return new Bounds(minX, minY, width, height);
	}
	
	/**
	 * Creates a new bounds instance grown by the given x and y amounts. The new bounds moves up and to the left by the
	 * amount it has grown and the width and height are expanded by twice the amount to grow. This creates the effect of
	 * the bounds retaining the center position it had previously.
	 *
	 * @param x the amount to grow in the x dimension
	 * @param y the amount to grow in the y dimension
	 * @return the resized bounds
	 */
	public Bounds grow(int x, int y)
	{
		return new Bounds(minX - x, minY - y, width + (x * 2), height + (y * 2));
	}
	
	/**
	 * Creates a new bounds instance with the origin x and y, as well as the width and height all multiplied by a multiplicator.
	 *
	 * @param multiplicator the value to multiply against the bounds values
	 * @return the multiplied bounds
	 */
	public Bounds multiply(float multiplicator)
	{
		return new Bounds((int) (minX * multiplicator), (int) (minY * multiplicator), (int) (width * multiplicator), (int) (height * multiplicator));
	}
	
	/**
	 * Creates a new bounds instance with the origin x and y, as well as the width and height all divided by a divisor (rounding down).
	 *
	 * @param divisor the value to divide the bounds values by
	 * @return the divided bounds
	 */
	public Bounds divideFloor(float divisor)
	{
		return new Bounds((int) (minX / divisor), (int) (minY / divisor), (int) (width / divisor), (int) (height / divisor));
	}
	
	/**
	 * Creates a new bounds instance with the origin x and y, as well as the width and height all divided by a divisor (rounding normally).
	 *
	 * @param divisor the value to divide the bounds values by
	 * @return the divided bounds
	 */
	public Bounds divideRound(float divisor)
	{
		return new Bounds(Math.round(minX / divisor), Math.round(minY / divisor), Math.round(width / divisor), Math.round(height / divisor));
	}
	
	/**
	 * Creates a new bounds instance with the origin x and y, as well as the width and height all divided by a divisor (rounding up).
	 *
	 * @param divisor the value to divide the bounds values by
	 * @return the divided bounds
	 */
	public Bounds divideCeil(float divisor)
	{
		return new Bounds((int) Math.ceil(minX / divisor), (int) Math.ceil(minY / divisor), (int) Math.ceil(width / divisor), (int) Math.ceil(height / divisor));
	}
}
