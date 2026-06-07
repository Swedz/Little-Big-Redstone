package net.swedz.little_big_redstone.microchip.object.logic.io;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAware;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTickingContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;

import java.util.Objects;
import java.util.Optional;

public final class LogicIO extends LogicComponent<LogicIO, LogicIOConfig> implements MicrochipAware
{
	public static final MapCodec<LogicIO> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					LogicIOConfig.CODEC.fieldOf("config").forGetter(LogicIO::config),
					DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicIO::color),
					Codec.INT.optionalFieldOf("output", 0).forGetter(LogicIO::output)
			)
			.apply(instance, LogicIO::new));
	
	public static final StreamCodec<ByteBuf, LogicIO> STREAM_CODEC = StreamCodec.composite(
			LogicIOConfig.STREAM_CODEC, LogicIO::config,
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicIO::color,
			ByteBufCodecs.VAR_INT, LogicIO::output,
			LogicIO::new
	);
	
	private int outputState;
	
	private LogicIO(LogicIOConfig config, Optional<DyeColor> color, int outputState)
	{
		super(config, color);
		this.outputState = outputState;
	}
	
	private LogicIO(Optional<DyeColor> color, int outputState)
	{
		super(color);
		this.outputState = outputState;
	}
	
	public LogicIO()
	{
		this(Optional.empty(), 0);
	}
	
	@Override
	public AwarenessType<?>[] awarenessTypes()
	{
		return new AwarenessType[]{
				AwarenessTypes.REDSTONE
		};
	}
	
	@Override
	protected void processTickInternal(LogicTickingContext context, int[] inputs)
	{
		boolean powerChanged = false;
		int originalOutputState = outputState;
		if(config.input())
		{
			int signal = context.awareness(AwarenessTypes.REDSTONE).getInputPower(config.direction());
			outputState = config.signalComparison().test(signal, config.signalStrength()) ? signal : 0;
		}
		else
		{
			outputState = inputs[0];
			var redstone = context.awareness(AwarenessTypes.REDSTONE);
			int signal = outputState > 0 ? (config.signalStrength() == 0 ? outputState : config.signalStrength()) : 0;
			if(redstone.setOutputPowered(config.direction(), signal))
			{
				powerChanged = true;
			}
		}
		if(powerChanged || outputState != originalOutputState)
		{
			context.markDirty(this);
		}
	}
	
	@Override
	public LogicType<LogicIO, LogicIOConfig> type()
	{
		return LBRLogicTypes.IO.get();
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
	protected void internalLoadFrom(LogicIO other)
	{
		outputState = other.outputState;
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
			   (o instanceof LogicIO other && Objects.equals(config, other.config) && Objects.equals(color, other.color));
	}
}