package net.swedz.little_big_redstone.microchip.logic.debug;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;

import java.util.Objects;
import java.util.Optional;

public final class LogicDebugger extends LogicComponent<LogicDebugger, LogicDebuggerConfig>
{
	public static final MapCodec<LogicDebugger> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicDebugger::color)
			)
			.apply(instance, LogicDebugger::new));
	
	public static final StreamCodec<ByteBuf, LogicDebugger> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicDebugger::color,
			LogicDebugger::new
	);
	
	private LogicDebugger(Optional<DyeColor> color)
	{
		super(color);
	}
	
	public LogicDebugger()
	{
		this(Optional.empty());
	}
	
	@Override
	protected LogicDebuggerConfig defaultConfig()
	{
		return LogicDebuggerConfig.INSTANCE;
	}
	
	@Override
	public LogicType<LogicDebugger> type()
	{
		return LogicTypes.DEBUGGER;
	}
	
	@Override
	protected void processTickInternal(LogicContext context, boolean[] inputs)
	{
	}
	
	@Override
	public boolean output(int index)
	{
		return false;
	}
	
	@Override
	protected void internalLoadFrom(LogicDebugger other)
	{
	}
	
	@Override
	protected void internalResetForPickup()
	{
	}
	
	@Override
	public LogicDebugger copy()
	{
		return new LogicDebugger(color);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.type(), color);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicDebugger other && Objects.equals(color, other.color));
	}
}
