package net.swedz.little_big_redstone.microchip.object.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.api.Bounds;
import net.swedz.little_big_redstone.microchip.object.MicrochipObject;
import net.swedz.little_big_redstone.microchip.object.MicrochipObjectContainerType;

import java.util.Optional;

public record LogicEntry(int slot, int x, int y, LogicComponent component) implements MicrochipObject
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
	
	@Override
	public MicrochipObjectContainerType containerType()
	{
		return MicrochipObjectContainerType.LOGIC_COMPONENT;
	}
	
	@Override
	public ItemStack toStack()
	{
		return component.type().toStack(component);
	}
	
	@Override
	public Bounds toBounds()
	{
		return new Bounds(x, y, this.size().widthPixels(), this.size().heightPixels());
	}
	
	@Override
	public Optional<DyeColor> color()
	{
		return component.color();
	}
	
	@Override
	public boolean setColor(Optional<DyeColor> color)
	{
		var original = component.color();
		component.setColor(color);
		return !original.equals(color);
	}
	
	public LogicGridSize size()
	{
		return component.size();
	}
}
