package net.swedz.little_big_redstone.microchip.object.logic.pulse;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;
import net.swedz.tesseract.neoforge.api.range.IntRange;

import java.util.List;
import java.util.Objects;

public final class PulseThrottlerConfig extends LogicConfig<PulseThrottlerConfig>
{
	public static final MapCodec<PulseThrottlerConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.LONG.optionalFieldOf("duration", 1L).forGetter((config) -> config.outputDuration),
					Codec.intRange(0, 15).optionalFieldOf("signal_strength", 0).forGetter((config) -> config.signalStrength)
			)
			.apply(instance, PulseThrottlerConfig::new));
	
	public static final StreamCodec<ByteBuf, PulseThrottlerConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_LONG, (config) -> config.outputDuration,
			ByteBufCodecs.VAR_INT, (config) -> config.signalStrength,
			PulseThrottlerConfig::new
	);
	
	public long outputDuration;
	
	public int signalStrength;
	
	private PulseThrottlerConfig(long outputDuration, int signalStrength)
	{
		this.outputDuration = outputDuration;
		this.signalStrength = signalStrength;
	}
	
	public PulseThrottlerConfig()
	{
		this(1, 0);
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
	public void appendHoverText(List<Component> lines)
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
	
	@Override
	protected void internalLoadFrom(PulseThrottlerConfig other)
	{
		outputDuration = other.outputDuration;
		signalStrength = other.signalStrength;
	}
	
	@Override
	public void resetForPickup()
	{
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(outputDuration, signalStrength);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof PulseThrottlerConfig other && outputDuration == other.outputDuration && signalStrength == other.signalStrength);
	}
}