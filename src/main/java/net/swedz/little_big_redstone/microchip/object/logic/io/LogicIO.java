package net.swedz.little_big_redstone.microchip.object.logic.io;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAware;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public final class LogicIO extends LogicComponent<LogicIO, LogicIOConfig> implements MicrochipAware
{
	public static final MapCodec<LogicIO> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					LogicIOConfig.CODEC.fieldOf("config").forGetter(LogicIO::config),
					DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicIO::color),
					Codec.BOOL.optionalFieldOf("output", false).forGetter(LogicIO::output)
			)
			.apply(instance, LogicIO::new));
	
	public static final StreamCodec<ByteBuf, LogicIO> STREAM_CODEC = StreamCodec.composite(
			LogicIOConfig.STREAM_CODEC, LogicIO::config,
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicIO::color,
			ByteBufCodecs.BOOL, LogicIO::output,
			LogicIO::new
	);
	
	private boolean outputState;
	
	private LogicIO(LogicIOConfig config, Optional<DyeColor> color, boolean outputState)
	{
		super(config, color);
		this.outputState = outputState;
	}
	
	private LogicIO(Optional<DyeColor> color, boolean outputState)
	{
		super(color);
		this.outputState = outputState;
	}
	
	public LogicIO()
	{
		this(Optional.empty(), false);
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
		boolean powerChanged = false;
		boolean originalOutputState = outputState;
		if(config.input)
		{
			int signal = context.awareness(AwarenessTypes.REDSTONE).getInputPower(config.direction);
			outputState = signal >= config.signalStrength;
		}
		else
		{
			outputState = inputs[0];
			var redstone = context.awareness(AwarenessTypes.REDSTONE);
			int signal = outputState ? config.signalStrength : 0;
			if(redstone.setOutputPowered(config.direction, signal))
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
	public LogicType<LogicIO> type()
	{
		return LogicTypes.IO;
	}
	
	@Override
	protected boolean outputInternal(int index)
	{
		return outputState;
	}
	
	public boolean output()
	{
		return this.output(0);
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_HELP_IO_PORT_1));
		lines.add(line(LBRText.LOGIC_HELP_IO_PORT_2));
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
