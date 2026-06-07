package net.swedz.little_big_redstone.microchip.object.logic.debug;

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
	public LogicType type()
	{
		return LBRLogicTypes.DEBUGGER.get();
	}
	
	@Override
	protected void processTickInternal(LogicTickingContext context, int[] inputs)
	{
	}
	
	@Override
	protected int outputInternal(int index)
	{
		return 0;
	}
	
	@Override
	protected void internalLoadFrom(LogicDebugger other)
	{
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