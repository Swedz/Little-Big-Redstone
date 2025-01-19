package net.swedz.little_big_redstone.microchip.logic.io;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.api.IntRange;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.LogicFactory;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;

import java.util.Objects;

public final class LogicIO extends LogicComponent<LogicIO, LogicIOConfig>
{
	public static final LogicFactory DEFAULT = () -> new LogicIO(new LogicIOConfig(true, Direction.NORTH), false);
	
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
	
	@Override
	protected void processTickInternal(LogicContext context, boolean[] inputs)
	{
		boolean originalOutputState = outputState;
		if(config.input)
		{
			outputState = context.isInputPowered(config.direction);
		}
		else
		{
			outputState = inputs[0];
			context.setOutputPowered(config.direction, outputState);
		}
		if(outputState != originalOutputState)
		{
			LBR.LOGGER.info("I/O port output state changed to: {}", outputState);
			context.markDirty();
		}
	}
	
	@Override
	public LogicType<LogicIO> type()
	{
		return LogicTypes.IO;
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return config.input ? new IntRange(0, 0) : new IntRange(1, 1);
	}
	
	@Override
	public int inputs()
	{
		return config.input ? 0 : 1;
	}
	
	@Override
	public IntRange outputsAllowed()
	{
		return config.input ? new IntRange(1, 1) : new IntRange(0, 0);
	}
	
	@Override
	public int outputs()
	{
		return config.input ? 1 : 0;
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
