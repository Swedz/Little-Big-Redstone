package net.swedz.little_big_redstone.microchip.object.logic.sequencer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicGridSize;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;
import net.swedz.tesseract.neoforge.api.range.IntRange;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.List;

public record LogicSequencerConfig(
		LogicSequencerMode mode,
		long outputDelay,
		boolean autoReset,
		boolean resetPort
) implements LogicConfig
{
	public static final LogicSequencerConfig DEFAULT = new LogicSequencerConfig(
			LogicSequencerMode.WEAK,
			20,
			false,
			false
	);
	
	public static final MapCodec<LogicSequencerConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					CodecHelper.forLowercaseEnum(LogicSequencerMode.class).optionalFieldOf("mode", LogicSequencerMode.WEAK).forGetter(LogicSequencerConfig::mode),
					Codec.LONG.optionalFieldOf("delay", 20L).forGetter(LogicSequencerConfig::outputDelay),
					Codec.BOOL.optionalFieldOf("auto_reset", false).forGetter(LogicSequencerConfig::autoReset),
					Codec.BOOL.optionalFieldOf("reset_port", false).forGetter(LogicSequencerConfig::resetPort)
			)
			.apply(instance, LogicSequencerConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicSequencerConfig> STREAM_CODEC = StreamCodec.composite(
			CodecHelper.forLowercaseEnumStream(LogicSequencerMode.class), LogicSequencerConfig::mode,
			ByteBufCodecs.VAR_LONG, LogicSequencerConfig::outputDelay,
			ByteBufCodecs.BOOL, LogicSequencerConfig::autoReset,
			ByteBufCodecs.BOOL, LogicSequencerConfig::resetPort,
			LogicSequencerConfig::new
	);
	
	@Override
	public LogicType type()
	{
		return LBRLogicTypes.SEQUENCER.get();
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(1, 2);
	}
	
	@Override
	public int inputs()
	{
		return resetPort ? 2 : 1;
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
	public LogicGridSize size()
	{
		return new LogicGridSize(2, 1);
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpSequencer1());
		lines.add(LBR.text().logicHelpSequencer2());
		lines.add(LBR.text().logicHelpSequencer3());
	}
	
	@Override
	public void appendConfigHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicConfigTooltipMode(mode));
		lines.add(LBR.text().logicConfigTooltipSequencerDelay(outputDelay));
		lines.add(LBR.text().logicConfigTooltipSequencerAutoReset(autoReset));
		lines.add(LBR.text().logicConfigTooltipSequencerResetPort(resetPort));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public LogicConfigMenuProvider<LogicSequencerConfig> getMenuProvider()
	{
		return new LogicSequencerConfigMenuProvider(this);
	}
}