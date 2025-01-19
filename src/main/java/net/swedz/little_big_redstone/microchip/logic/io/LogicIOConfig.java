package net.swedz.little_big_redstone.microchip.logic.io;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.logic.LogicConfig;

import java.util.Objects;

public final class LogicIOConfig extends LogicConfig<LogicIOConfig>
{
	public static final MapCodec<LogicIOConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.BOOL.fieldOf("input").forGetter((config) -> config.input),
					Direction.CODEC.fieldOf("direction").forGetter((config) -> config.direction)
			)
			.apply(instance, LogicIOConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicIOConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, (config) -> config.input,
			Direction.STREAM_CODEC, (config) -> config.direction,
			LogicIOConfig::new
	);
	
	public boolean input;
	
	public Direction direction;
	
	public LogicIOConfig(boolean input, Direction direction)
	{
		this.input = input;
		this.direction = direction;
	}
	
	@Override
	public LogicIOConfig copy()
	{
		return new LogicIOConfig(input, direction);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(input, direction);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicIOConfig other && input == other.input && direction == other.direction);
	}
}
