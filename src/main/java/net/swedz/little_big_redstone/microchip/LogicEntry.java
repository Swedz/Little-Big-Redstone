package net.swedz.little_big_redstone.microchip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.api.Bounds;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.logic.LogicGridSize;

public record LogicEntry(int slot, int x, int y, LogicComponent component)
{
	public static final Codec<LogicEntry> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.INT.fieldOf("slot").forGetter(LogicEntry::slot),
					Codec.INT.fieldOf("x").forGetter(LogicEntry::x),
					Codec.INT.fieldOf("y").forGetter(LogicEntry::y),
					LogicComponent.CODEC.fieldOf("logic").forGetter(LogicEntry::component)
			)
			.apply(instance, LogicEntry::new));
	
	public static final StreamCodec<ByteBuf, LogicEntry> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, LogicEntry::slot,
			ByteBufCodecs.VAR_INT, LogicEntry::x,
			ByteBufCodecs.VAR_INT, LogicEntry::y,
			LogicComponent.STREAM_CODEC, LogicEntry::component,
			LogicEntry::new
	);
	
	public LogicEntry
	{
		component = component.copy();
	}
	
	public ItemStack toStack()
	{
		return component.type().toStack(component);
	}
	
	public Bounds toBounds()
	{
		return new Bounds(x, y, component.size().widthPixels(), component.size().heightPixels());
	}
	
	public boolean contains(int x, int y)
	{
		return this.toBounds().contains(x, y);
	}
	
	public boolean contains(int x, int y, LogicGridSize size)
	{
		return this.toBounds().overlaps(new Bounds(x, y, size.widthPixels(), size.heightPixels()));
	}
}
