package net.swedz.little_big_redstone.microchip.object.logic.io;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponents;
import net.swedz.little_big_redstone.microchip.object.logic.LogicMode;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicComparisonMode;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;
import net.swedz.tesseract.neoforge.api.range.IntRange;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.List;
import java.util.Objects;

public final class LogicIOConfig extends LogicConfig<LogicIOConfig>
{
	public static final MapCodec<LogicIOConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.BOOL.optionalFieldOf("input", true).forGetter((config) -> config.input),
					Direction.CODEC.optionalFieldOf("direction", Direction.NORTH).forGetter((config) -> config.direction),
					Codec.intRange(0, 15).optionalFieldOf("signal_strength", 0).forGetter((config) -> config.signalStrength),
					CodecHelper.forLowercaseEnum(LogicComparisonMode.class).optionalFieldOf("signal_comparison", LogicComparisonMode.GREATER_THAN_OR_EQUAL_TO).forGetter((config) -> config.signalComparison),
					CodecHelper.forLowercaseEnum(LogicPowerOutputType.class).optionalFieldOf("power_type", LogicPowerOutputType.WEAK).forGetter((config) -> config.powerType)
			)
			.apply(instance, (input, direction, signalStrength, precise, strong) -> new LogicIOConfig(true, input, direction, signalStrength, precise, strong)));
	
	public static final StreamCodec<ByteBuf, LogicIOConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, (config) -> config.valid,
			ByteBufCodecs.BOOL, (config) -> config.input,
			Direction.STREAM_CODEC, (config) -> config.direction,
			ByteBufCodecs.INT, (config) -> config.signalStrength,
			CodecHelper.forEnumStream(LogicComparisonMode.class), (config) -> config.signalComparison,
			CodecHelper.forEnumStream(LogicPowerOutputType.class), (config) -> config.powerType,
			LogicIOConfig::new
	);
	
	public boolean input;
	
	public Direction direction;
	
	public int signalStrength;
	public LogicComparisonMode signalComparison;
	
	public LogicPowerOutputType powerType;
	
	private LogicIOConfig(boolean valid, boolean input, Direction direction, int signalStrength, LogicComparisonMode signalComparison, LogicPowerOutputType powerType)
	{
		this.valid = valid;
		this.input = input;
		this.direction = direction;
		this.signalStrength = Mth.clamp(signalStrength, input ? 1 : 0, 15);
		this.signalComparison = signalComparison;
		this.powerType = powerType;
	}
	
	public LogicIOConfig()
	{
		this(true, true, Direction.NORTH, 1, LogicComparisonMode.GREATER_THAN_OR_EQUAL_TO, LogicPowerOutputType.WEAK);
	}
	
	@Override
	protected boolean calculateValidity(LogicComponents components)
	{
		for(var entry : components)
		{
			if(entry.component().config() != this && entry.component().config() instanceof LogicIOConfig entryConfig &&
			   input != entryConfig.input && direction == entryConfig.direction)
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return input ? new IntRange(0, 0) : new IntRange(1, 1);
	}
	
	@Override
	public int inputs()
	{
		return input ? 0 : 1;
	}
	
	@Override
	public IntRange outputsAllowed()
	{
		return input ? new IntRange(1, 1) : new IntRange(0, 0);
	}
	
	@Override
	public int outputs()
	{
		return input ? 1 : 0;
	}
	
	@Override
	public void appendHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicConfigTooltipMode(input ? LogicMode.input() : LogicMode.output()));
		lines.add(LBR.text().logicConfigTooltipDirection(direction));
		if(input)
		{
			lines.add(LBR.text().logicConfigTooltipSignalComparison(signalComparison, signalStrength));
		}
		else
		{
			lines.add(signalStrength == 0 ?
					LBR.text().logicConfigTooltipSignal(LBR.text().pass()) :
					LBR.text().logicConfigTooltipSignal(signalStrength));
			lines.add(LBR.text().logicConfigTooltipIoPowerOutput(powerType));
		}
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public LogicConfigMenuProvider getMenuProvider()
	{
		return new LogicIOConfigMenuProvider(this);
	}
	
	@Override
	protected void internalLoadFrom(LogicIOConfig other)
	{
		input = other.input;
		direction = other.direction;
		signalStrength = other.signalStrength;
		signalComparison = other.signalComparison;
		powerType = other.powerType;
	}
	
	@Override
	public void resetForPickup()
	{
		valid = true;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(input, direction, signalStrength, signalComparison, powerType);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicIOConfig other && input == other.input && direction == other.direction && signalStrength == other.signalStrength && signalComparison == other.signalComparison && powerType == other.powerType);
	}
}