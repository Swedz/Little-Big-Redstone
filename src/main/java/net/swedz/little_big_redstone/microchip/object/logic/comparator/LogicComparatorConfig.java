package net.swedz.little_big_redstone.microchip.object.logic.comparator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicAccumulationMode;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicComparisonMode;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;
import net.swedz.tesseract.neoforge.api.range.IntRange;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.List;
import java.util.Objects;

public final class LogicComparatorConfig extends LogicConfig<LogicComparatorConfig>
{
	public static final MapCodec<LogicComparatorConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					CodecHelper.forLowercaseEnum(LogicAccumulationMode.class).optionalFieldOf("mode", LogicAccumulationMode.ALL).forGetter((config) -> config.mode),
					Codec.intRange(0, 15).optionalFieldOf("signal_strength", 0).forGetter((config) -> config.signalStrength),
					CodecHelper.forLowercaseEnum(LogicComparisonMode.class).optionalFieldOf("signal_comparison", LogicComparisonMode.GREATER_THAN_OR_EQUAL_TO).forGetter((config) -> config.signalComparison),
					Codec.intRange(1, 10).optionalFieldOf("inputs", 1).forGetter((config) -> config.inputs)
			)
			.apply(instance, LogicComparatorConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicComparatorConfig> STREAM_CODEC = StreamCodec.composite(
			CodecHelper.forEnumStream(LogicAccumulationMode.class), (config) -> config.mode,
			ByteBufCodecs.VAR_INT, (config) -> config.signalStrength,
			CodecHelper.forEnumStream(LogicComparisonMode.class), (config) -> config.signalComparison,
			ByteBufCodecs.VAR_INT, (config) -> config.inputs,
			LogicComparatorConfig::new
	);
	
	public LogicAccumulationMode mode;
	public int                   signalStrength;
	public LogicComparisonMode   signalComparison;
	public int                   inputs;
	
	public LogicComparatorConfig(LogicAccumulationMode mode, int signalStrength, LogicComparisonMode signalComparison, int inputs)
	{
		this.mode = mode;
		this.signalStrength = signalStrength;
		this.signalComparison = signalComparison;
		this.inputs = inputs;
	}
	
	public LogicComparatorConfig()
	{
		this(LogicAccumulationMode.ALL, 0, LogicComparisonMode.EQUAL_TO, 1);
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(1, 11);
	}
	
	@Override
	public int inputs()
	{
		return inputs + (signalStrength == 0 ? 1 : 0);
	}
	
	@Override
	public IntRange outputsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public int outputs()
	{
		return 1;
	}
	
	@Override
	public void appendHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicConfigTooltipMode(mode));
		
		lines.add(signalStrength == 0 ?
				LBR.text().logicConfigTooltipSignalComparison(signalComparison, LBR.text().pass()) :
				LBR.text().logicConfigTooltipSignalComparison(signalComparison, signalStrength));
		
		lines.add(LBR.text().logicConfigTooltipInputs(inputs));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public LogicConfigMenuProvider<LogicComparatorConfig> getMenuProvider()
	{
		return new LogicComparatorConfigMenuProvider(this);
	}
	
	@Override
	protected void internalLoadFrom(LogicComparatorConfig other)
	{
		mode = other.mode;
		signalStrength = other.signalStrength;
		signalComparison = other.signalComparison;
		inputs = other.inputs;
	}
	
	@Override
	public void resetForPickup()
	{
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(mode, signalStrength, signalComparison, inputs);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicComparatorConfig other && mode == other.mode && signalStrength == other.signalStrength && signalComparison == other.signalComparison && inputs == other.inputs);
	}
}
