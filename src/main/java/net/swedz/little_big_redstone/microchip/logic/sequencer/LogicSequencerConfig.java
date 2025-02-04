package net.swedz.little_big_redstone.microchip.logic.sequencer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.LBRTooltips;
import net.swedz.little_big_redstone.api.IntRange;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfigMenuBuilder;

import java.util.List;
import java.util.Objects;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public final class LogicSequencerConfig extends LogicConfig<LogicSequencerConfig>
{
	public static final MapCodec<LogicSequencerConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.LONG.fieldOf("delay").forGetter((config) -> config.outputDelay),
					Codec.LONG.fieldOf("duration").forGetter((config) -> config.outputDuration),
					Codec.BOOL.fieldOf("requires_continuous_power").forGetter((config) -> config.requiresContinuousPower)
			)
			.apply(instance, LogicSequencerConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicSequencerConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_LONG, (config) -> config.outputDelay,
			ByteBufCodecs.VAR_LONG, (config) -> config.outputDuration,
			ByteBufCodecs.BOOL, (config) -> config.requiresContinuousPower,
			LogicSequencerConfig::new
	);
	
	public long outputDelay, outputDuration;
	
	public boolean requiresContinuousPower;
	
	public LogicSequencerConfig(long outputDelay, long outputDuration, boolean requiresContinuousPower)
	{
		this.outputDelay = outputDelay;
		this.outputDuration = outputDuration;
		this.requiresContinuousPower = requiresContinuousPower;
	}
	
	public LogicSequencerConfig()
	{
		this(20, 0, false);
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
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_SEQUENCER_DELAY).arg(outputDelay));
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_SEQUENCER_DURATION).arg(outputDuration));
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_SEQUENCER_CONTINUOUS).arg(requiresContinuousPower, LBRTooltips.BOOLEAN_YES_NO_PARSER));
	}
	
	@Override
	public void buildMenu(LogicConfigMenuBuilder builder)
	{
		builder.addSlider(LBRText.LOGIC_CONFIG_BUTTON_LABEL_SEQUENCER_DELAY.text(), Component.empty(), LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_DELAY.text(), 0, 0, 160, 18, 1, 60 * 20, outputDelay, 1, 0, true, (value) -> outputDelay = value.intValue());
		
		builder.addSlider(LBRText.LOGIC_CONFIG_BUTTON_LABEL_SEQUENCER_DURATION.text(), Component.empty(), LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_DURATION.text(), 0, 23, 160, 18, 0, 60 * 20, outputDuration, 1, 0, true, (value) -> outputDuration = value.intValue());
		
		builder.addCheckbox(LBRText.LOGIC_CONFIG_BUTTON_LABEL_SEQUENCER_CONTINUOUS.text().withColor(0x3E3E3E), LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_CONTINUOUS.text(), 0, 46, requiresContinuousPower, (value) -> requiresContinuousPower = value);
	}
	
	@Override
	public void loadFrom(LogicSequencerConfig other)
	{
		outputDelay = other.outputDelay;
		outputDuration = other.outputDuration;
		requiresContinuousPower = other.requiresContinuousPower;
	}
	
	@Override
	public void resetForPickup()
	{
	}
	
	@Override
	public LogicSequencerConfig copy()
	{
		return new LogicSequencerConfig(outputDelay, outputDuration, requiresContinuousPower);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(outputDelay, outputDuration, requiresContinuousPower);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicSequencerConfig other && outputDelay == other.outputDelay && outputDuration == other.outputDuration && requiresContinuousPower == other.requiresContinuousPower);
	}
}
