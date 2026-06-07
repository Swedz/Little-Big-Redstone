package net.swedz.little_big_redstone.microchip.object.logic.comparator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicAccumulationMode;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicComparisonMode;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;
import net.swedz.tesseract.neoforge.api.range.IntRange;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.List;

public record LogicComparatorConfig(
		LogicAccumulationMode mode,
		int signalStrength,
		LogicComparisonMode signalComparison,
		int outputOverride,
		int inputs
) implements LogicConfig
{
	public static final LogicComparatorConfig DEFAULT = new LogicComparatorConfig(
			LogicAccumulationMode.ALL,
			0,
			LogicComparisonMode.EQUAL_TO,
			0,
			1
	);
	
	public static final MapCodec<LogicComparatorConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					CodecHelper.forLowercaseEnum(LogicAccumulationMode.class).optionalFieldOf("mode", DEFAULT.mode()).forGetter(LogicComparatorConfig::mode),
					Codec.intRange(0, 15).optionalFieldOf("signal_strength", DEFAULT.signalStrength()).forGetter(LogicComparatorConfig::signalStrength),
					CodecHelper.forLowercaseEnum(LogicComparisonMode.class).optionalFieldOf("signal_comparison", DEFAULT.signalComparison()).forGetter(LogicComparatorConfig::signalComparison),
					Codec.intRange(0, 15).optionalFieldOf("output_override", DEFAULT.outputOverride()).forGetter(LogicComparatorConfig::outputOverride),
					Codec.intRange(1, 10).optionalFieldOf("inputs", DEFAULT.inputs()).forGetter(LogicComparatorConfig::inputs)
			)
			.apply(instance, LogicComparatorConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicComparatorConfig> STREAM_CODEC = StreamCodec.composite(
			CodecHelper.forEnumStream(LogicAccumulationMode.class), LogicComparatorConfig::mode,
			ByteBufCodecs.VAR_INT, LogicComparatorConfig::signalStrength,
			CodecHelper.forEnumStream(LogicComparisonMode.class), LogicComparatorConfig::signalComparison,
			ByteBufCodecs.VAR_INT, LogicComparatorConfig::outputOverride,
			ByteBufCodecs.VAR_INT, LogicComparatorConfig::inputs,
			LogicComparatorConfig::new
	);
	
	@Override
	public LogicType type()
	{
		return LBRLogicTypes.COMPARATOR.get();
	}
	
	@Override
	public IntRange inputPortsAllowed()
	{
		return new IntRange(1, 11);
	}
	
	@Override
	public int inputPorts()
	{
		return inputs + (signalStrength == 0 ? 1 : 0);
	}
	
	@Override
	public IntRange outputPortsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public int outputPorts()
	{
		return 1;
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpComparator1());
		lines.add(LBR.text().logicHelpComparator2());
	}
	
	@Override
	public void appendConfigHoverText(List<Component> lines)
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
}
