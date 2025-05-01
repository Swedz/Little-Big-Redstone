package net.swedz.little_big_redstone.microchip.logic.pulse;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.api.IntRange;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfigMenuBuilder;

import java.util.List;
import java.util.Objects;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public final class PulseThrottlerConfig extends LogicConfig<PulseThrottlerConfig>
{
	public static final MapCodec<PulseThrottlerConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.LONG.fieldOf("duration").forGetter((config) -> config.outputDuration)
			)
			.apply(instance, PulseThrottlerConfig::new));
	
	public static final StreamCodec<ByteBuf, PulseThrottlerConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_LONG, (config) -> config.outputDuration,
			PulseThrottlerConfig::new
	);
	
	public long outputDuration;
	
	public PulseThrottlerConfig(long outputDuration)
	{
		this.outputDuration = outputDuration;
	}
	
	public PulseThrottlerConfig()
	{
		this(1);
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
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_DURATION).arg(outputDuration));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public void buildMenu(LogicConfigMenuBuilder builder)
	{
		builder.addSlider(LBRText.LOGIC_CONFIG_BUTTON_LABEL_DURATION.text(), Component.empty(), LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_DURATION.text(), 0, 0, 160, 18, 0, 60 * 20, outputDuration, 1, 0, true, (value) -> outputDuration = value.intValue());
	}
	
	@Override
	public void loadFrom(PulseThrottlerConfig other)
	{
		outputDuration = other.outputDuration;
	}
	
	@Override
	public void resetForPickup()
	{
	}
	
	@Override
	public PulseThrottlerConfig copy()
	{
		return new PulseThrottlerConfig(outputDuration);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(outputDuration);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof PulseThrottlerConfig other && outputDuration == other.outputDuration);
	}
}
