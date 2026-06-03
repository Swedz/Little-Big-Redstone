package net.swedz.little_big_redstone.microchip.object.logic.debug;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.tesseract.neoforge.api.range.IntRange;

public record LogicDebuggerConfig() implements LogicConfig<LogicDebuggerConfig>
{
	public static final LogicDebuggerConfig DEFAULT = new LogicDebuggerConfig();
	
	public static final MapCodec<LogicDebuggerConfig> CODEC = MapCodec.unit(DEFAULT);
	
	public static final StreamCodec<ByteBuf, LogicDebuggerConfig> STREAM_CODEC = StreamCodec.unit(DEFAULT);
	
	@Override
	public LogicType<?, LogicDebuggerConfig> type()
	{
		return LogicTypes.DEBUGGER;
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(0, 0);
	}
	
	@Override
	public int inputs()
	{
		return 0;
	}
	
	@Override
	public IntRange outputsAllowed()
	{
		return new IntRange(0, 0);
	}
	
	@Override
	public int outputs()
	{
		return 0;
	}
}