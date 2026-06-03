package net.swedz.little_big_redstone.microchip.object.logic.io;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponents;
import net.swedz.little_big_redstone.microchip.object.logic.LogicMode;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicComparisonMode;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;
import net.swedz.tesseract.neoforge.api.range.IntRange;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.List;

public record LogicIOConfig(
		boolean input,
		Direction direction,
		int signalStrength,
		LogicComparisonMode signalComparison,
		LogicPowerOutputType powerType
) implements LogicConfig<LogicIOConfig>
{
	public static final MapCodec<LogicIOConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.BOOL.optionalFieldOf("input", true).forGetter(LogicIOConfig::input),
					Direction.CODEC.optionalFieldOf("direction", Direction.NORTH).forGetter(LogicIOConfig::direction),
					Codec.intRange(0, 15).optionalFieldOf("signal_strength", 0).forGetter(LogicIOConfig::signalStrength),
					CodecHelper.forLowercaseEnum(LogicComparisonMode.class).optionalFieldOf("signal_comparison", LogicComparisonMode.GREATER_THAN_OR_EQUAL_TO).forGetter(LogicIOConfig::signalComparison),
					CodecHelper.forLowercaseEnum(LogicPowerOutputType.class).optionalFieldOf("power_type", LogicPowerOutputType.WEAK).forGetter(LogicIOConfig::powerType)
			)
			.apply(instance, LogicIOConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicIOConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, LogicIOConfig::input,
			Direction.STREAM_CODEC, LogicIOConfig::direction,
			ByteBufCodecs.INT, LogicIOConfig::signalStrength,
			CodecHelper.forEnumStream(LogicComparisonMode.class), LogicIOConfig::signalComparison,
			CodecHelper.forEnumStream(LogicPowerOutputType.class), LogicIOConfig::powerType,
			LogicIOConfig::new
	);
	
	public static final LogicIOConfig DEFAULT = new LogicIOConfig(
			true,
			Direction.NORTH,
			1,
			LogicComparisonMode.GREATER_THAN_OR_EQUAL_TO,
			LogicPowerOutputType.WEAK
	);
	
	@Override
	public LogicType<?, LogicIOConfig> type()
	{
		return LogicTypes.IO;
	}
	
	@Override
	public boolean checkValid(LogicComponents components)
	{
		for(var entry : components)
		{
			if(entry.component().config() != this &&
			   entry.component().config() instanceof LogicIOConfig entryConfig &&
			   input != entryConfig.input &&
			   direction == entryConfig.direction)
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
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpIOPort1());
		lines.add(LBR.text().logicHelpIOPort2());
	}
	
	@Override
	public void appendConfigHoverText(List<Component> lines)
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
}