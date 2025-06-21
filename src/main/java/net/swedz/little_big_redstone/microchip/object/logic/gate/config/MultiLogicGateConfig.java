package net.swedz.little_big_redstone.microchip.object.logic.gate.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfigMenuBuilder;
import net.swedz.tesseract.neoforge.api.range.IntRange;

import java.util.List;
import java.util.Objects;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public final class MultiLogicGateConfig extends LogicConfig<MultiLogicGateConfig>
{
	public static final Codec<MultiLogicGateConfig> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.INT.optionalFieldOf("input_count", 2).forGetter((config) -> config.inputs)
			)
			.apply(instance, MultiLogicGateConfig::new));
	
	public static final StreamCodec<ByteBuf, MultiLogicGateConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, (config) -> config.inputs,
			MultiLogicGateConfig::new
	);
	
	public int inputs;
	
	private MultiLogicGateConfig(int inputs)
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
		return new IntRange(2, 10);
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
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_INPUTS).arg(inputs));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public void buildMenu(LogicConfigMenuBuilder builder)
	{
		builder.addSlider(LBRText.LOGIC_CONFIG_BUTTON_LABEL_INPUTS.text(), Component.empty(), LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_INPUTS.text(), 0, 0, 160, 18, this.inputsAllowed().min(), this.inputsAllowed().max(), inputs, 1, 0, (value) -> inputs = value.intValue());
	}
	
	@Override
	protected void internalLoadFrom(MultiLogicGateConfig other)
	{
		inputs = this.inputsAllowed().clamp(other.inputs);
	}
	
	@Override
	public void resetForPickup()
	{
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
