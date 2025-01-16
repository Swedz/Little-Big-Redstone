package net.swedz.redstone_circuitry.microchip.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public final class NANDGate implements LogicGate
{
	public static final NANDGate INSTANCE = new NANDGate();
	
	public static final MapCodec<NANDGate> CODEC = MapCodec.unit(INSTANCE);
	
	public static final StreamCodec<ByteBuf, NANDGate> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	
	private NANDGate()
	{
	}
	
	@Override
	public LogicGateType type()
	{
		return LogicGates.NAND;
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
				return true;
			}
		}
		return false;
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
