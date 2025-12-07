package net.swedz.little_big_redstone.microchip.object.logic.gate.config;

import com.mojang.serialization.Codec;
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
		lines.add(LBR.text().logicConfigTooltipInputs(inputs));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public LogicConfigMenuProvider<MultiLogicGateConfig> getMenuProvider()
	{
		return new MultiLogicGateConfigMenuProvider(this);
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