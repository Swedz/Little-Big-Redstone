package net.swedz.little_big_redstone.microchip.logic.sequencer;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.api.IntRange;
import net.swedz.little_big_redstone.microchip.logic.Logic;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.LogicGridSize;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;

public final class LogicSequencer extends Logic<LogicSequencer>
{
	public static final LogicSequencer DEFAULT = new LogicSequencer(20, 0, false, 0, false);
	
	public static final MapCodec<LogicSequencer> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.LONG.fieldOf("delay").forGetter(LogicSequencer::delay),
					Codec.LONG.fieldOf("duration").forGetter(LogicSequencer::duration),
					Codec.BOOL.fieldOf("requires_continuous_power").forGetter(LogicSequencer::requiresContinuousPower),
					Codec.LONG.fieldOf("processed_ticks").forGetter(LogicSequencer::processedTicks),
					Codec.BOOL.fieldOf("output").forGetter(LogicSequencer::output)
			)
			.apply(instance, LogicSequencer::new));
	
	public static final StreamCodec<ByteBuf, LogicSequencer> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_LONG, LogicSequencer::delay,
			ByteBufCodecs.VAR_LONG, LogicSequencer::duration,
			ByteBufCodecs.BOOL, LogicSequencer::requiresContinuousPower,
			ByteBufCodecs.VAR_LONG, LogicSequencer::processedTicks,
			ByteBufCodecs.BOOL, LogicSequencer::output,
			LogicSequencer::new
	);
	
	private long outputDelay, outputDuration;
	private boolean requiresContinuousPower;
	
	private long processedTicks;
	private boolean outputState;
	
	private LogicSequencer(long outputDelay, long outputDuration, boolean requiresContinuousPower, long processedTicks, boolean outputState)
	{
		this.outputDelay = outputDelay;
		this.outputDuration = outputDuration;
		this.requiresContinuousPower = requiresContinuousPower;
		this.processedTicks = processedTicks;
		this.outputState = outputState;
	}
	
	public long delay()
	{
		return outputDelay;
	}
	
	public long duration()
	{
		return outputDuration;
	}
	
	public boolean requiresContinuousPower()
	{
		return requiresContinuousPower;
	}
	
	public long processedTicks()
	{
		return processedTicks;
	}
	
	public float processedPercentage()
	{
		return Math.min((float) processedTicks / (float) outputDelay, 1);
	}
	
	@Override
	protected void processTickInternal(LogicContext context, boolean[] inputs)
	{
		boolean originalOutput = outputState;
		boolean input = inputs[0];
		boolean output = false;
		
		if(requiresContinuousPower)
		{
			if(input)
			{
				processedTicks++;
			}
			else
			{
				processedTicks = 0;
			}
		}
		else if(processedTicks > 0)
		{
			processedTicks++;
		}
		
		if(processedTicks >= outputDelay)
		{
			output = true;
			
			if(processedTicks > outputDelay + outputDuration)
			{
				processedTicks = 0;
				output = false;
			}
		}
		
		outputState = output;
		if(output != originalOutput)
		{
			context.flagChanged();
		}
	}
	
	@Override
	public LogicType<LogicSequencer> type()
	{
		return LogicTypes.SEQUENCER;
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public int inputs()
	{
		return 1;
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
	public boolean output(int index)
	{
		return outputState;
	}
	
	public boolean output()
	{
		return outputState;
	}
	
	@Override
	public LogicGridSize size()
	{
		return new LogicGridSize(2, 1);
	}
	
	@Override
	public void resetForPickup()
	{
		processedTicks = 0;
		outputState = false;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hashCode(outputDelay, processedTicks);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicSequencer other && outputDelay == other.outputDelay && processedTicks == other.processedTicks);
	}
}
