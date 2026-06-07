package net.swedz.little_big_redstone.microchip.object.logic.debug;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.tesseract.neoforge.api.range.IntRange;

public record LogicDebuggerConfig() implements LogicConfig
{
	public static final LogicDebuggerConfig DEFAULT = new LogicDebuggerConfig();
	
	public static final MapCodec<LogicDebuggerConfig> CODEC = MapCodec.unit(DEFAULT);
	
	public static final StreamCodec<ByteBuf, LogicDebuggerConfig> STREAM_CODEC = StreamCodec.unit(DEFAULT);
	
	@Override
	public LogicType type()
	{
		return LBRLogicTypes.DEBUGGER.get();
	}
	
	@Override
	public IntRange inputPortsAllowed()
	{
		return new IntRange(0, 0);
	}
	
	@Override
	public int inputPorts()
	{
		return 0;
	}
	
	@Override
	public IntRange outputPortsAllowed()
	{
		return new IntRange(0, 0);
	}
	
	@Override
	public int outputPorts()
	{
		return 0;
	}
}