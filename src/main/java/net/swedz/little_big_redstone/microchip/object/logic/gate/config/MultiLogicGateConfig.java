package net.swedz.little_big_redstone.microchip.object.logic.gate.config;

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
import java.util.function.Function;

public abstract class MultiLogicGateConfig<C extends MultiLogicGateConfig<C>> implements LogicConfig
{
	public static <C extends MultiLogicGateConfig<C>> MapCodec<C> codec(Function<Integer, C> creator)
	{
		return RecordCodecBuilder.mapCodec((instance) -> instance
				.group(
						Codec.INT.optionalFieldOf("input_count", 2).forGetter(MultiLogicGateConfig::inputs)
				)
				.apply(instance, creator));
	}
	
	public static <C extends MultiLogicGateConfig<C>> StreamCodec<ByteBuf, C> streamCodec(Function<Integer, C> creator)
	{
		return StreamCodec.composite(
				ByteBufCodecs.VAR_INT, MultiLogicGateConfig::inputs,
				creator
		);
	}
	
	private final int inputs;
	
	public MultiLogicGateConfig(int inputs)
	{
		this.inputs = this.inputsAllowed().clamp(inputs);
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
	public void appendConfigHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicConfigTooltipInputs(inputs));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	protected abstract C mutateConfig(int inputs);
	
	@Override
	public LogicConfigMenuProvider<C> getMenuProvider()
	{
		return new MultiLogicGateConfigMenuProvider<>((C) this, this::mutateConfig);
	}
	
	@Override
	public int hashCode()
	{
		return Integer.hashCode(inputs);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o != null &&
			   Objects.equals(o.getClass(), this.getClass()) &&
			   inputs == ((MultiLogicGateConfig) o).inputs;
	}
}