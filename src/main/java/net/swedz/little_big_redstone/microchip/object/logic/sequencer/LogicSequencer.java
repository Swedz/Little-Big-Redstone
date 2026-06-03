package net.swedz.little_big_redstone.microchip.object.logic.sequencer;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTickingContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

import java.util.Optional;

public final class LogicSequencer extends LogicComponent<LogicSequencer, LogicSequencerConfig>
{
	public static final MapCodec<LogicSequencer> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					LogicSequencerConfig.CODEC.fieldOf("config").forGetter(LogicSequencer::config),
					DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicSequencer::color),
					Codec.LONG.optionalFieldOf("processed_ticks", 1L).forGetter(LogicSequencer::processedTicks),
					Codec.INT.optionalFieldOf("last_input", 0).forGetter((l) -> l.lastInput),
					Codec.BOOL.optionalFieldOf("output", false).forGetter(LogicSequencer::output)
			)
			.apply(instance, LogicSequencer::new));
	
	public static final StreamCodec<ByteBuf, LogicSequencer> STREAM_CODEC = StreamCodec.composite(
			LogicSequencerConfig.STREAM_CODEC, LogicSequencer::config,
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicSequencer::color,
			ByteBufCodecs.VAR_LONG, LogicSequencer::processedTicks,
			ByteBufCodecs.VAR_INT, (l) -> l.lastInput,
			ByteBufCodecs.BOOL, LogicSequencer::output,
			LogicSequencer::new
	);
	
	private long processedTicks;
	
	private int lastInput;
	
	private boolean outputState;
	
	private LogicSequencer(LogicSequencerConfig config, Optional<DyeColor> color, long processedTicks, int lastInput, boolean outputState)
	{
		super(config, color);
		this.processedTicks = processedTicks;
		this.lastInput = lastInput;
		this.outputState = outputState;
	}
	
	private LogicSequencer(Optional<DyeColor> color, long processedTicks, int lastInput, boolean outputState)
	{
		super(color);
		this.processedTicks = processedTicks;
		this.lastInput = lastInput;
		this.outputState = outputState;
	}
	
	public LogicSequencer()
	{
		this(Optional.empty(), 0, 0, false);
	}
	
	public long processedTicks()
	{
		return processedTicks;
	}
	
	public float processedPercentage()
	{
		return Math.min((float) processedTicks / (float) config.outputDelay(), 1);
	}
	
	@Override
	protected void processTickInternal(LogicTickingContext context, int[] inputs)
	{
		long originalProcessedTicks = processedTicks;
		boolean originalOutputState = outputState;
		boolean input = inputs[0] > 0;
		boolean output = false;
		
		if(config.resetPort() && inputs[1] > 0)
		{
			processedTicks = 0;
		}
		else
		{
			if(input)
			{
				lastInput = inputs[0];
				processedTicks++;
			}
			else if(processedTicks > 0)
			{
				if(config.mode() == LogicSequencerMode.WEAK)
				{
					processedTicks++;
				}
				else if(config.mode() == LogicSequencerMode.STRONG)
				{
					processedTicks--;
				}
			}
		}
		
		if(processedTicks >= config.outputDelay())
		{
			processedTicks = config.autoReset() ? 0 : config.outputDelay();
			output = true;
		}
		
		outputState = output;
		if(originalProcessedTicks != processedTicks || output != originalOutputState)
		{
			context.markDirty(this);
		}
	}
	
	@Override
	public LogicType<LogicSequencer, LogicSequencerConfig> type()
	{
		return LogicTypes.SEQUENCER;
	}
	
	@Override
	protected int outputInternal(int index)
	{
		return outputState ? lastInput : 0;
	}
	
	public boolean output()
	{
		return this.output(0) > 0;
	}
	
	@Override
	protected void internalLoadFrom(LogicSequencer other)
	{
		processedTicks = other.processedTicks;
		lastInput = other.lastInput;
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
			   (o instanceof LogicSequencer other && Objects.equal(config, other.config) && Objects.equal(color, other.color) && processedTicks == other.processedTicks);
	}
}