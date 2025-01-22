package net.swedz.little_big_redstone.microchip.logic.io;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.LBRTooltips;
import net.swedz.little_big_redstone.microchip.logic.LogicConfig;
import net.swedz.little_big_redstone.microchip.logic.LogicConfigMenuBuilder;

import java.util.List;
import java.util.Objects;

import static net.swedz.little_big_redstone.LBRTextLine.*;

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
	public void appendHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_CONFIGURATION_IO_MODE).arg(input, LBRTooltips.INPUT_OUTPUT_PARSER));
		lines.add(line(LBRText.LOGIC_CONFIGURATION_IO_DIRECTION).arg(direction, LBRTooltips.DIRECTION_PARSER));
	}
	
	@Override
	public void buildMenu(int leftPos, int topPos, LogicConfigMenuBuilder builder)
	{
		// TODO
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
