package net.swedz.redstone_circuitry.microchip.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public final class ANDGate implements LogicGate
{
	public static final ANDGate INSTANCE = new ANDGate();
	
	public static final MapCodec<ANDGate> CODEC = MapCodec.unit(INSTANCE);
	
	public static final StreamCodec<ByteBuf, ANDGate> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	
	private ANDGate()
	{
	}
	
	@Override
	public LogicGateType type()
	{
		return LogicGates.AND;
	}
	
	@Override
	public int inputCount()
	{
		return 3;
	}
	
	@Override
	public boolean process(Level level, BlockPos pos, boolean[] inputs)
	{
		for(boolean input : inputs)
		{
			if(!input)
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode()
	{
		return this.type().hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o;
	}
}
