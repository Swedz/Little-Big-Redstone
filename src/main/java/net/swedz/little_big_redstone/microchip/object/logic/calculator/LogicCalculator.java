package net.swedz.little_big_redstone.microchip.object.logic.calculator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicGridSize;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class LogicCalculator extends LogicComponent<LogicCalculator, LogicCalculatorConfig>
{
	public static final MapCodec<LogicCalculator> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					LogicCalculatorConfig.CODEC.fieldOf("config").forGetter(LogicCalculator::config),
					DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicCalculator::color),
					Codec.INT.optionalFieldOf("output", 0).forGetter(LogicCalculator::output)
			)
			.apply(instance, LogicCalculator::new));
	
	public static final StreamCodec<ByteBuf, LogicCalculator> STREAM_CODEC = StreamCodec.composite(
			LogicCalculatorConfig.STREAM_CODEC, LogicCalculator::config,
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicCalculator::color,
			ByteBufCodecs.VAR_INT, LogicCalculator::output,
			LogicCalculator::new
	);
	
	private int outputState;
	
	private LogicCalculator(LogicCalculatorConfig config, Optional<DyeColor> color, int outputState)
	{
		super(config, color);
		this.outputState = outputState;
	}
	
	private LogicCalculator(Optional<DyeColor> color, int outputState)
	{
		super(color);
		this.outputState = outputState;
	}
	
	public LogicCalculator()
	{
		this(Optional.empty(), 0);
	}
	
	@Override
	protected LogicCalculatorConfig defaultConfig()
	{
		return new LogicCalculatorConfig();
	}
	
	@Override
	public LogicType<LogicCalculator> type()
	{
		return LogicTypes.CALCULATOR;
	}
	
	@Override
	protected void processTickInternal(LogicContext context, int[] inputs)
	{
		int originalOutputState = outputState;
		
		int total = inputs[0];
		for(int index = 1; index < inputs.length; index++)
		{
			int signal = inputs[index];
			if(config.mode == LogicCalculatorMode.ADDITION)
			{
				total += signal;
			}
			else if(config.mode == LogicCalculatorMode.SUBTRACTION)
			{
				total -= signal;
			}
		}
		
		outputState = Mth.clamp(total, 0, 15);
		if(outputState != originalOutputState)
		{
			context.markDirty(this);
		}
	}
	
	@Override
	protected int outputInternal(int index)
	{
		return outputState;
	}
	
	public int output()
	{
		return this.output(0);
	}
	
	@Override
	public LogicGridSize size()
	{
		int inputs = this.inputs();
		return new LogicGridSize(1, Math.max(1, inputs / 2));
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpCalculator1());
		lines.add(LBR.text().logicHelpCalculator2());
	}
	
	@Override
	protected void internalLoadFrom(LogicCalculator other)
	{
		outputState = other.outputState;
	}
	
	@Override
	protected void internalResetForPickup()
	{
		outputState = 0;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.type(), config, color);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicCalculator other && Objects.equals(config, other.config) && Objects.equals(color, other.color));
	}
}
