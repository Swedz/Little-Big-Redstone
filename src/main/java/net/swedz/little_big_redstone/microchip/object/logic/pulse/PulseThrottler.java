package net.swedz.little_big_redstone.microchip.object.logic.pulse;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTickingContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;

import java.util.Optional;

public final class PulseThrottler extends LogicComponent<PulseThrottler, PulseThrottlerConfig>
{
	public static final MapCodec<PulseThrottler> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					PulseThrottlerConfig.CODEC.fieldOf("config").forGetter(PulseThrottler::config),
					DyeColor.CODEC.optionalFieldOf("color").forGetter(PulseThrottler::color),
					Codec.BOOL.optionalFieldOf("last_input", false).forGetter(PulseThrottler::lastInput),
					Codec.LONG.optionalFieldOf("processed_ticks", 0L).forGetter(PulseThrottler::processedTicks),
					Codec.INT.optionalFieldOf("stored_signal", 0).forGetter(PulseThrottler::storedSignal),
					Codec.INT.optionalFieldOf("output", 0).forGetter(PulseThrottler::output)
			)
			.apply(instance, PulseThrottler::new));
	
	public static final StreamCodec<ByteBuf, PulseThrottler> STREAM_CODEC = StreamCodec.composite(
			PulseThrottlerConfig.STREAM_CODEC, PulseThrottler::config,
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), PulseThrottler::color,
			ByteBufCodecs.BOOL, PulseThrottler::lastInput,
			ByteBufCodecs.VAR_LONG, PulseThrottler::processedTicks,
			ByteBufCodecs.VAR_INT, PulseThrottler::storedSignal,
			ByteBufCodecs.VAR_INT, PulseThrottler::output,
			PulseThrottler::new
	);
	
	private boolean lastInputState;
	private long    processedTicks;
	
	private int storedSignal;
	private int outputState;
	
	private PulseThrottler(PulseThrottlerConfig config, Optional<DyeColor> color, boolean lastInputState, long processedTicks, int storedSignal, int outputState)
	{
		super(config, color);
		this.lastInputState = lastInputState;
		this.processedTicks = processedTicks;
		this.storedSignal = storedSignal;
		this.outputState = outputState;
	}
	
	private PulseThrottler(Optional<DyeColor> color, boolean lastInputState, long processedTicks, int storedSignal, int outputState)
	{
		super(color);
		this.lastInputState = lastInputState;
		this.processedTicks = processedTicks;
		this.storedSignal = storedSignal;
		this.outputState = outputState;
	}
	
	public PulseThrottler()
	{
		this(Optional.empty(), false, 0, 0, 0);
	}
	
	@Override
	public LogicType<PulseThrottler, PulseThrottlerConfig> type()
	{
		return LBRLogicTypes.PULSE_THROTTLER.get();
	}
	
	public boolean lastInput()
	{
		return lastInputState;
	}
	
	public long processedTicks()
	{
		return processedTicks;
	}
	
	public int storedSignal()
	{
		return storedSignal;
	}
	
	@Override
	protected void processTickInternal(LogicTickingContext context, int[] inputs)
	{
		int originalOutputState = outputState;
		
		int input = inputs[0];
		boolean output = false;
		
		boolean changed = false;
		
		if(config.outputDuration() > 0)
		{
			if(processedTicks >= config.outputDuration())
			{
				processedTicks = 0;
				output = false;
				changed = true;
			}
			else if(processedTicks > 0)
			{
				processedTicks++;
				output = true;
				changed = true;
			}
			else if(!lastInputState && input > 0)
			{
				storedSignal = input;
				processedTicks++;
				output = true;
				changed = true;
			}
		}
		else
		{
			output = input > 0;
			storedSignal = input;
		}
		
		outputState = output ? (config.signalStrength() == 0 ? storedSignal : config.signalStrength()) : 0;
		if(outputState == 0)
		{
			storedSignal = 0;
		}
		if(changed || originalOutputState != outputState)
		{
			context.markDirty(this);
		}
		lastInputState = input > 0;
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
	protected void internalLoadFrom(PulseThrottler other)
	{
		lastInputState = other.lastInputState;
		processedTicks = other.processedTicks;
		storedSignal = other.storedSignal;
		outputState = other.outputState;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.type(), config, color, processedTicks);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof PulseThrottler other && Objects.equal(config, other.config) && Objects.equal(color, other.color) && processedTicks == other.processedTicks);
	}
}