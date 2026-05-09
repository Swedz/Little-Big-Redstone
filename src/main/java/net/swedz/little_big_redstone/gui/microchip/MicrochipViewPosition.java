package net.swedz.little_big_redstone.gui.microchip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.microchip.MicrochipSize;

public final class MicrochipViewPosition
{
	public static final Codec<MicrochipViewPosition> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.DOUBLE.fieldOf("x").forGetter(MicrochipViewPosition::x),
					Codec.DOUBLE.fieldOf("y").forGetter(MicrochipViewPosition::y),
					Codec.FLOAT.fieldOf("zoom").forGetter(MicrochipViewPosition::zoom)
			)
			.apply(instance, MicrochipViewPosition::new));
	
	public static final StreamCodec<ByteBuf, MicrochipViewPosition> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.DOUBLE, MicrochipViewPosition::x,
			ByteBufCodecs.DOUBLE, MicrochipViewPosition::y,
			ByteBufCodecs.FLOAT, MicrochipViewPosition::zoom,
			MicrochipViewPosition::new
	);
	
	private MicrochipSize size;
	private int           originX, originY;
	
	private double x, y;
	
	private float zoom = 1;
	
	public MicrochipViewPosition(double x, double y, float zoom)
	{
		this.x = x;
		this.y = y;
		this.zoom = zoom;
	}
	
	public MicrochipViewPosition()
	{
	}
	
	public void init(MicrochipSize size, int x, int y)
	{
		this.size = size;
		this.originX = x;
		this.originY = y;
		
		this.x = this.clampOffset(this.x, MicrochipBlockEntity.CIRCUIT_BOUNDS.width());
		this.y = this.clampOffset(this.y, MicrochipBlockEntity.CIRCUIT_BOUNDS.height());
		this.zoom = this.clampZoom(this.zoom);
	}
	
	public double x()
	{
		return x;
	}
	
	public double y()
	{
		return y;
	}
	
	public void pan(double dragX, double dragY)
	{
		double ox = x - (dragX / size.scale() / zoom);
		double oy = y - (dragY / size.scale() / zoom);
		x = this.clampOffset(ox, MicrochipBlockEntity.CIRCUIT_BOUNDS.width());
		y = this.clampOffset(oy, MicrochipBlockEntity.CIRCUIT_BOUNDS.height());
	}
	
	public float zoom()
	{
		return zoom;
	}
	
	public void zoom(float amount, double mouseX, double mouseY)
	{
		float newZoom = this.clampZoom(zoom + amount);
		if(newZoom == zoom)
		{
			return;
		}
		
		double localMouseX = mouseX - originX;
		double localMouseY = mouseY - originY;
		double boardMouseX = size.boardCoord(localMouseX, zoom, x);
		double boardMouseY = size.boardCoord(localMouseY, zoom, y);
		
		zoom = Math.round(newZoom * 100) / 100f;
		
		double ox = size.boardCoord(-localMouseX, zoom, boardMouseX);
		double oy = size.boardCoord(-localMouseY, zoom, boardMouseY);
		
		x = this.clampOffset(ox, MicrochipBlockEntity.CIRCUIT_BOUNDS.width());
		y = this.clampOffset(oy, MicrochipBlockEntity.CIRCUIT_BOUNDS.height());
	}
	
	private float clampZoom(float zoom)
	{
		if(zoom < 1)
		{
			return 1;
		}
		else if(zoom > 2.5)
		{
			return 2.5f;
		}
		return zoom;
	}
	
	private double clampOffset(double offset, double bounds)
	{
		double circuitBounds = bounds / size.scale();
		if(offset < 0)
		{
			offset = 0;
		}
		double max = circuitBounds - (circuitBounds / zoom);
		if(offset > max)
		{
			offset = max;
		}
		return offset;
	}
	
	public MicrochipViewPosition copy()
	{
		return new MicrochipViewPosition(x, y, zoom);
	}
}
