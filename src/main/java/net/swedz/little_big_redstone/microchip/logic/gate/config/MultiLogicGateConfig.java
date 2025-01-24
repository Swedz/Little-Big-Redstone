package net.swedz.little_big_redstone.microchip.logic.gate.config;

import com.mojang.serialization.Codec;
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

public final class MultiLogicGateConfig extends LogicConfig<MultiLogicGateConfig>
{
	public static final Codec<MultiLogicGateConfig> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.INT.fieldOf("input_count").forGetter((config) -> config.inputs)
			)
			.apply(instance, MultiLogicGateConfig::new));
	
	public static final StreamCodec<ByteBuf, MultiLogicGateConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, (config) -> config.inputs,
			MultiLogicGateConfig::new
	);
	
	public int inputs;
	
	public MultiLogicGateConfig(int inputs)
	{
		this.inputs = inputs;
	}
	
	public MultiLogicGateConfig()
	{
		this.inputs = this.inputsAllowed().min();
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(2, 16);
	}
	
	@Override
	public int inputs()
	{
		return inputs;
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
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_GATE_INPUTS).arg(inputs));
	}
	
	@Override
	public void buildMenu(LogicConfigMenuBuilder builder)
	{
		builder.addSlider(LBRText.LOGIC_CONFIG_BUTTON_LABEL_GATE_INPUTS.text(), Component.empty(), LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_GATE_INPUTS.text(), 0, 0, 160, 18, this.inputsAllowed().min(), this.inputsAllowed().max(), inputs, 1, 0, true, (value) -> inputs = value.intValue());
	}
	
	@Override
	public void loadFrom(MultiLogicGateConfig other)
	{
		inputs = this.inputsAllowed().clamp(other.inputs);
	}
	
	@Override
	public MultiLogicGateConfig copy()
	{
		return new MultiLogicGateConfig(inputs);
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
			   (o instanceof MultiLogicGateConfig other && inputs == other.inputs);
	}
}
