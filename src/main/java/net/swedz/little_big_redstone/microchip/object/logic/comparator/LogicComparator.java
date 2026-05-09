package net.swedz.little_big_redstone.microchip.object.logic.comparator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicGridSize;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicAccumulationMode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class LogicComparator extends LogicComponent<LogicComparator, LogicComparatorConfig>
{
	public static final MapCodec<LogicComparator> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					LogicComparatorConfig.CODEC.fieldOf("config").forGetter(LogicComparator::config),
					DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicComparator::color),
					Codec.INT.optionalFieldOf("output", 0).forGetter(LogicComparator::output)
			)
			.apply(instance, LogicComparator::new));
	
	public static final StreamCodec<ByteBuf, LogicComparator> STREAM_CODEC = StreamCodec.composite(
			LogicComparatorConfig.STREAM_CODEC, LogicComparator::config,
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicComparator::color,
			ByteBufCodecs.VAR_INT, LogicComparator::output,
			LogicComparator::new
	);
	
	private int outputState;
	
	private LogicComparator(LogicComparatorConfig config, Optional<DyeColor> color, int outputState)
	{
		super(config, color);
		this.outputState = outputState;
	}
	
	private LogicComparator(Optional<DyeColor> color, int outputState)
	{
		super(color);
		this.outputState = outputState;
	}
	
	public LogicComparator()
	{
		this(Optional.empty(), 0);
	}
	
	@Override
	protected LogicComparatorConfig defaultConfig()
	{
		return new LogicComparatorConfig();
	}
	
	@Override
	public LogicType<LogicComparator> type()
	{
		return LogicTypes.COMPARATOR;
	}
	
	@Override
	protected void processTickInternal(LogicContext context, int[] inputs)
	{
		int originalOutputState = outputState;
		
		boolean isPass = config.signalStrength == 0;
		int against = isPass ? inputs[0] : config.signalStrength;
		boolean matches = config.mode == LogicAccumulationMode.ALL;
		
		for(int index = (isPass ? 1 : 0); index < inputs.length; index++)
		{
			int input = inputs[index];
			
			if(config.signalComparison.test(input, against))
			{
				if(config.mode == LogicAccumulationMode.ANY)
				{
					matches = true;
					break;
				}
			}
			else
			{
				if(config.mode == LogicAccumulationMode.ALL)
				{
					matches = false;
					break;
				}
			}
		}
		
		outputState = matches ? against : 0;
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
		lines.add(LBR.text().logicHelpComparator1());
		lines.add(LBR.text().logicHelpComparator2());
	}
	
	@Override
	protected void internalLoadFrom(LogicComparator other)
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
			   (o instanceof LogicComparator other && Objects.equals(config, other.config) && Objects.equals(color, other.color));
	}
}
