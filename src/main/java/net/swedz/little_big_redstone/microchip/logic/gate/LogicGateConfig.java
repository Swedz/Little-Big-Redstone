package net.swedz.little_big_redstone.microchip.logic.gate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfigMenuBuilder;

import java.util.List;
import java.util.Objects;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public final class LogicGateConfig extends LogicConfig<LogicGateConfig>
{
	public static final Codec<LogicGateConfig> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.INT.fieldOf("input_count").forGetter((config) -> config.inputs)
			)
			.apply(instance, LogicGateConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicGateConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, (config) -> config.inputs,
			LogicGateConfig::new
	);
	
	public int inputs;
	
	public LogicGateConfig(int inputs)
	{
		this.inputs = inputs;
	}
	
	@Override
	public void appendHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_GATE_INPUTS).arg(inputs));
	}
	
	@Override
	public void buildMenu(LogicConfigMenuBuilder builder)
	{
		builder.addSlider(LBRText.LOGIC_CONFIG_BUTTON_GATE_INPUTS.text(), Component.empty(), 0, 0, 160, 18, 2, 16, inputs, 1, 0, true, (value) -> inputs = value.intValue());
	}
	
	@Override
	public void loadFrom(LogicGateConfig other)
	{
		inputs = other.inputs;
	}
	
	@Override
	public LogicGateConfig copy()
	{
		return new LogicGateConfig(inputs);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(inputs);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicGateConfig other && inputs == other.inputs);
	}
}
