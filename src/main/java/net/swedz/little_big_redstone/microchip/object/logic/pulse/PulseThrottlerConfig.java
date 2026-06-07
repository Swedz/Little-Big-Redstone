package net.swedz.little_big_redstone.microchip.object.logic.pulse;

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
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;
import net.swedz.tesseract.neoforge.api.range.IntRange;

import java.util.List;

public record PulseThrottlerConfig(
		long outputDuration,
		int signalStrength
) implements LogicConfig
{
	public static final PulseThrottlerConfig DEFAULT = new PulseThrottlerConfig(
			1,
			0
	);
	
	public static final MapCodec<PulseThrottlerConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.LONG.optionalFieldOf("duration", DEFAULT.outputDuration()).forGetter(PulseThrottlerConfig::outputDuration),
					Codec.intRange(0, 15).optionalFieldOf("signal_strength", DEFAULT.signalStrength()).forGetter(PulseThrottlerConfig::signalStrength)
			)
			.apply(instance, PulseThrottlerConfig::new));
	
	public static final StreamCodec<ByteBuf, PulseThrottlerConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_LONG, PulseThrottlerConfig::outputDuration,
			ByteBufCodecs.VAR_INT, PulseThrottlerConfig::signalStrength,
			PulseThrottlerConfig::new
	);
	
	@Override
	public LogicType type()
	{
		return LBRLogicTypes.PULSE_THROTTLER.get();
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public int inputs()
	{
		return 1;
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
		lines.add(LBR.text().logicHelpPulseThrottler1());
		lines.add(LBR.text().logicHelpPulseThrottler2());
	}
	
	@Override
	public void appendConfigHoverText(List<Component> lines)
	{
		lines.add(outputDuration == 0 ?
				LBR.text().logicConfigTooltipDuration(LBR.text().indefinite()) :
				LBR.text().logicConfigTooltipDuration(outputDuration));
		
		lines.add(signalStrength == 0 ?
				LBR.text().logicConfigTooltipSignal(LBR.text().pass()) :
				LBR.text().logicConfigTooltipSignal(signalStrength));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public LogicConfigMenuProvider getMenuProvider()
	{
		return new PulseThrottlerConfigMenuProvider(this);
	}
}