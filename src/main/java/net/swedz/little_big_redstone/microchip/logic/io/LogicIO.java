package net.swedz.little_big_redstone.microchip.logic.io;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAware;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;

import java.util.Objects;

public final class LogicIO extends LogicComponent<LogicIO, LogicIOConfig> implements MicrochipAware
{
	public static final MapCodec<LogicIO> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					LogicIOConfig.CODEC.fieldOf("config").forGetter(LogicIO::config),
					Codec.BOOL.fieldOf("output").forGetter(LogicIO::output)
			)
			.apply(instance, LogicIO::new));
	
	public static final StreamCodec<ByteBuf, LogicIO> STREAM_CODEC = StreamCodec.composite(
			LogicIOConfig.STREAM_CODEC, LogicIO::config,
			ByteBufCodecs.BOOL, LogicIO::output,
			LogicIO::new
	);
	
	private boolean outputState;
	
	private LogicIO(LogicIOConfig config, boolean outputState)
	{
		super(config);
		this.outputState = outputState;
	}
	
	private LogicIO(boolean outputState)
	{
		super();
		this.outputState = outputState;
	}
	
	public LogicIO()
	{
		this(false);
	}
	
	@Override
	public AwarenessType<?>[] awarenessTypes()
	{
		return new AwarenessType[]{
				AwarenessTypes.REDSTONE
		};
	}
	
	@Override
	protected LogicIOConfig defaultConfig()
	{
		return new LogicIOConfig();
	}
	
	@Override
	protected void processTickInternal(LogicContext context, boolean[] inputs)
	{
		boolean originalOutputState = outputState;
		if(config.input)
		{
			outputState = context.awareness(AwarenessTypes.REDSTONE).isInputPowered(config.direction);
		}
		else
		{
			outputState = inputs[0];
			context.awareness(AwarenessTypes.REDSTONE).setOutputPowered(config.direction, outputState);
		}
		if(outputState != originalOutputState)
		{
			context.markDirty(this);
		}
	}
	
	@Override
	public LogicType<LogicIO> type()
	{
		return LogicTypes.IO;
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
	protected void internalLoadFrom(LogicIO other)
	{
		outputState = other.outputState;
	}
	
	@Override
	public void internalResetForPickup()
	{
		outputState = false;
	}
	
	@Override
	public LogicIO copy()
	{
		return new LogicIO(config.copy(), outputState);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(config);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicIO other && Objects.equals(config, other.config));
	}
}
