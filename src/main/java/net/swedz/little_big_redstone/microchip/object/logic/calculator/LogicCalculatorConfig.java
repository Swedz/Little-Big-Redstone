package net.swedz.little_big_redstone.microchip.object.logic.calculator;

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
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.List;
import java.util.Objects;

public final class LogicCalculatorConfig extends LogicConfig<LogicCalculatorConfig>
{
	public static final MapCodec<LogicCalculatorConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					CodecHelper.forLowercaseEnum(LogicCalculatorMode.class).optionalFieldOf("mode", LogicCalculatorMode.ADDITION).forGetter((config) -> config.mode),
					Codec.intRange(2, 10).optionalFieldOf("inputs", 2).forGetter((config) -> config.inputs)
			)
			.apply(instance, LogicCalculatorConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicCalculatorConfig> STREAM_CODEC = StreamCodec.composite(
			CodecHelper.forEnumStream(LogicCalculatorMode.class), (config) -> config.mode,
			ByteBufCodecs.VAR_INT, (config) -> config.inputs,
			LogicCalculatorConfig::new
	);
	
	public LogicCalculatorMode mode;
	
	public int inputs;
	
	public LogicCalculatorConfig(LogicCalculatorMode mode, int inputs)
	{
		this.mode = mode;
		this.inputs = inputs;
	}
	
	public LogicCalculatorConfig()
	{
		this(LogicCalculatorMode.ADDITION, 2);
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
		lines.add(LBR.text().logicConfigTooltipMode(mode));
		
		lines.add(LBR.text().logicConfigTooltipInputs(inputs));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public LogicConfigMenuProvider<LogicCalculatorConfig> getMenuProvider()
	{
		return new LogicCalculatorConfigMenuProvider(this);
	}
	
	@Override
	protected void internalLoadFrom(LogicCalculatorConfig other)
	{
		mode = other.mode;
		inputs = other.inputs;
	}
	
	@Override
	public void resetForPickup()
	{
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(mode, inputs);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicCalculatorConfig other && mode == other.mode && inputs == other.inputs);
	}
}
