package net.swedz.little_big_redstone.microchip.logic.sequencer;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.LogicGridSize;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;

import java.util.List;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public final class LogicSequencer extends LogicComponent<LogicSequencer, LogicSequencerConfig>
{
	public static final MapCodec<LogicSequencer> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					LogicSequencerConfig.CODEC.fieldOf("config").forGetter(LogicSequencer::config),
					Codec.LONG.fieldOf("processed_ticks").forGetter(LogicSequencer::processedTicks),
					Codec.BOOL.fieldOf("output").forGetter(LogicSequencer::output)
			)
			.apply(instance, LogicSequencer::new));
	
	public static final StreamCodec<ByteBuf, LogicSequencer> STREAM_CODEC = StreamCodec.composite(
			LogicSequencerConfig.STREAM_CODEC, LogicSequencer::config,
			ByteBufCodecs.VAR_LONG, LogicSequencer::processedTicks,
			ByteBufCodecs.BOOL, LogicSequencer::output,
			LogicSequencer::new
	);
	
	private long processedTicks;
	
	private boolean outputState;
	
	private LogicSequencer(LogicSequencerConfig config, long processedTicks, boolean outputState)
	{
		super(config);
		this.processedTicks = processedTicks;
		this.outputState = outputState;
	}
	
	private LogicSequencer(long processedTicks, boolean outputState)
	{
		super();
		this.processedTicks = processedTicks;
		this.outputState = outputState;
	}
	
	public LogicSequencer()
	{
		this(0, false);
	}
	
	@Override
	protected LogicSequencerConfig defaultConfig()
	{
		return new LogicSequencerConfig();
	}
	
	public long processedTicks()
	{
		return processedTicks;
	}
	
	public float processedPercentage()
	{
		return Math.min((float) processedTicks / (float) config.outputDelay, 1);
	}
	
	@Override
	protected void processTickInternal(LogicContext context, boolean[] inputs)
	{
		long originalProcessedTicks = processedTicks;
		boolean originalOutput = outputState;
		boolean input = inputs[0];
		boolean output = false;
		
		if(config.requiresContinuousPower)
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
		else if(input || processedTicks > 0)
		{
			processedTicks++;
		}
		
		if(processedTicks >= config.outputDelay)
		{
			output = true;
			
			if(processedTicks > config.outputDelay + config.outputDuration)
			{
				processedTicks = 0;
				output = false;
			}
		}
		
		outputState = output;
		if(originalProcessedTicks != processedTicks || output != originalOutput)
		{
			context.markDirty(this);
		}
	}
	
	@Override
	public LogicType<LogicSequencer> type()
	{
		return LogicTypes.SEQUENCER;
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
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_HELP_SEQUENCER_1));
		lines.add(line(LBRText.LOGIC_HELP_SEQUENCER_2));
		lines.add(line(LBRText.LOGIC_HELP_SEQUENCER_3));
	}
	
	@Override
	protected void internalLoadFrom(LogicSequencer other)
	{
		processedTicks = other.processedTicks;
		outputState = other.outputState;
	}
	
	@Override
	public void internalResetForPickup()
	{
		processedTicks = 0;
		outputState = false;
	}
	
	@Override
	public LogicSequencer copy()
	{
		return new LogicSequencer(config.copy(), processedTicks, outputState);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hashCode(config, processedTicks);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicSequencer other && Objects.equal(config, other.config) && processedTicks == other.processedTicks);
	}
}
