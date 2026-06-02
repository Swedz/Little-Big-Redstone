package net.swedz.little_big_redstone.microchip.object.logic.reader;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicComparisonMode;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;
import net.swedz.tesseract.neoforge.api.range.IntRange;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.List;

public record LogicReaderConfig(
		LogicReaderMode mode,
		Direction direction,
		LogicReaderThreshold fillThreshold,
		int signalThreshold,
		LogicComparisonMode comparison
) implements LogicConfig<LogicReaderConfig>
{
	public static final LogicReaderConfig DEFAULT = new LogicReaderConfig(
			LogicReaderMode.ITEM,
			Direction.NORTH,
			LogicReaderThreshold.DEFAULT,
			1,
			LogicComparisonMode.GREATER_THAN_OR_EQUAL_TO
	);
	
	public static final MapCodec<LogicReaderConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					CodecHelper.forLowercaseEnum(LogicReaderMode.class).optionalFieldOf("mode", LogicReaderMode.ITEM).forGetter(LogicReaderConfig::mode),
					Direction.CODEC.optionalFieldOf("direction", Direction.NORTH).forGetter(LogicReaderConfig::direction),
					LogicReaderThreshold.CODEC.optionalFieldOf("fill_threshold", LogicReaderThreshold.DEFAULT).forGetter(LogicReaderConfig::fillThreshold),
					Codec.intRange(1, 15).optionalFieldOf("signal_threshold", 1).forGetter(LogicReaderConfig::signalThreshold),
					CodecHelper.forLowercaseEnum(LogicComparisonMode.class).optionalFieldOf("comparison", LogicComparisonMode.GREATER_THAN_OR_EQUAL_TO).forGetter(LogicReaderConfig::comparison)
			)
			.apply(instance, LogicReaderConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicReaderConfig> STREAM_CODEC = StreamCodec.composite(
			CodecHelper.forLowercaseEnumStream(LogicReaderMode.class), LogicReaderConfig::mode,
			Direction.STREAM_CODEC, LogicReaderConfig::direction,
			LogicReaderThreshold.STREAM_CODEC, LogicReaderConfig::fillThreshold,
			ByteBufCodecs.INT, LogicReaderConfig::signalThreshold,
			CodecHelper.forEnumStream(LogicComparisonMode.class), LogicReaderConfig::comparison,
			LogicReaderConfig::new
	);
	
	@Override
	public LogicType<?, LogicReaderConfig> type()
	{
		return LogicTypes.READER;
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(0, 0);
	}
	
	@Override
	public int inputs()
	{
		return 0;
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
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpReader1());
		lines.add(LBR.text().logicHelpReader2());
	}
	
	@Override
	public void appendConfigHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicConfigTooltipMode(mode));
		lines.add(LBR.text().logicConfigTooltipDirection(direction));
		lines.add(mode.readsSignal() ?
				LBR.text().logicConfigTooltipReaderSignalComparison(comparison, signalThreshold) :
				LBR.text().logicConfigTooltipReaderFillComparison(comparison, fillThreshold));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public LogicConfigMenuProvider getMenuProvider()
	{
		return new LogicReaderConfigMenuProvider(this);
	}
}