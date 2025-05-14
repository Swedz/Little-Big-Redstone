package net.swedz.little_big_redstone.microchip.object.logic.pulse;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

import java.util.List;
import java.util.Optional;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public final class PulseThrottler extends LogicComponent<PulseThrottler, PulseThrottlerConfig>
{
	public static final MapCodec<PulseThrottler> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					PulseThrottlerConfig.CODEC.fieldOf("config").forGetter(PulseThrottler::config),
					DyeColor.CODEC.optionalFieldOf("color").forGetter(PulseThrottler::color),
					Codec.BOOL.optionalFieldOf("last_input", false).forGetter(PulseThrottler::lastInput),
					Codec.LONG.optionalFieldOf("processed_ticks", 0L).forGetter(PulseThrottler::processedTicks),
					Codec.BOOL.optionalFieldOf("output", false).forGetter(PulseThrottler::output)
			)
			.apply(instance, PulseThrottler::new));
	
	public static final StreamCodec<ByteBuf, PulseThrottler> STREAM_CODEC = StreamCodec.composite(
			PulseThrottlerConfig.STREAM_CODEC, PulseThrottler::config,
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), PulseThrottler::color,
			ByteBufCodecs.BOOL, PulseThrottler::lastInput,
			ByteBufCodecs.VAR_LONG, PulseThrottler::processedTicks,
			ByteBufCodecs.BOOL, PulseThrottler::output,
			PulseThrottler::new
	);
	
	private boolean lastInputState;
	private long    processedTicks;
	
	private boolean outputState;
	
	private PulseThrottler(PulseThrottlerConfig config, Optional<DyeColor> color, boolean lastInputState, long processedTicks, boolean outputState)
	{
		super(config, color);
		this.lastInputState = lastInputState;
		this.processedTicks = processedTicks;
		this.outputState = outputState;
	}
	
	private PulseThrottler(Optional<DyeColor> color, boolean lastInputState, long processedTicks, boolean outputState)
	{
		super(color);
		this.lastInputState = lastInputState;
		this.processedTicks = processedTicks;
		this.outputState = outputState;
	}
	
	public PulseThrottler()
	{
		this(Optional.empty(), false, 0, false);
	}
	
	@Override
	protected PulseThrottlerConfig defaultConfig()
	{
		return new PulseThrottlerConfig();
	}
	
	@Override
	public LogicType<PulseThrottler> type()
	{
		return LogicTypes.PULSE_THROTTLER;
	}
	
	public boolean lastInput()
	{
		return lastInputState;
	}
	
	public long processedTicks()
	{
		return processedTicks;
	}
	
	@Override
	protected void processTickInternal(LogicContext context, boolean[] inputs)
	{
		boolean input = inputs[0];
		boolean output = false;
		
		boolean changed = false;
		
		if(processedTicks >= config.outputDuration)
		{
			processedTicks = 0;
			output = false;
			changed = true;
		}
		else if(!lastInputState && input)
		{
			processedTicks++;
			output = true;
			changed = true;
		}
		else if(processedTicks > 0)
		{
			processedTicks++;
			output = true;
			changed = true;
		}
		
		outputState = output;
		if(changed)
		{
			context.markDirty(this);
		}
		lastInputState = input;
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
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_HELP_PULSE_THROTTLER));
	}
	
	@Override
	protected void internalLoadFrom(PulseThrottler other)
	{
		lastInputState = other.lastInputState;
		processedTicks = other.processedTicks;
		outputState = other.outputState;
	}
	
	@Override
	protected void internalResetForPickup()
	{
		lastInputState = false;
		processedTicks = 0;
		outputState = false;
	}
	
	@Override
	public PulseThrottler copy()
	{
		return new PulseThrottler(config.copy(), color, lastInputState, processedTicks, outputState);
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
