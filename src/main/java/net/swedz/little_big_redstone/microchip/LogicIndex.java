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

public record LogicIndex(int slot, int x, int y, LogicComponent logic, LogicOutputPorts outputPorts)
{
	public static final Codec<LogicIndex> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.INT.fieldOf("slot").forGetter(LogicIndex::slot),
					Codec.INT.fieldOf("x").forGetter(LogicIndex::x),
					Codec.INT.fieldOf("y").forGetter(LogicIndex::y),
					LogicComponent.CODEC.fieldOf("logic").forGetter(LogicIndex::logic),
					LogicOutputPorts.CODEC.fieldOf("output_ports").forGetter(LogicIndex::outputPorts)
			)
			.apply(instance, LogicIndex::new));
	
	public static final StreamCodec<ByteBuf, LogicIndex> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, LogicIndex::slot,
			ByteBufCodecs.VAR_INT, LogicIndex::x,
			ByteBufCodecs.VAR_INT, LogicIndex::y,
			LogicComponent.STREAM_CODEC, LogicIndex::logic,
			LogicOutputPorts.STREAM_CODEC, LogicIndex::outputPorts,
			LogicIndex::new
	);
	
	public LogicIndex
	{
		logic = logic.copy();
	}
	
	public ItemStack toStack()
	{
		return logic.type().toStack(logic);
	}
	
	public Bounds bounds()
	{
		return new Bounds(x, y, logic.size().widthPixels(), logic.size().heightPixels());
	}
	
	public boolean contains(int x, int y)
	{
		return this.bounds().contains(x, y);
	}
	
	public boolean contains(int x, int y, LogicGridSize size)
	{
		return this.bounds().overlaps(new Bounds(x, y, size.widthPixels(), size.heightPixels()));
	}
}
