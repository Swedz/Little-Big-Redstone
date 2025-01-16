package net.swedz.redstone_circuitry.microchip.gate.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.swedz.redstone_circuitry.microchip.gate.LogicGate;
import net.swedz.redstone_circuitry.microchip.gate.LogicGateType;
import net.swedz.redstone_circuitry.microchip.gate.LogicGates;

public final class NORGate implements LogicGate
{
	public static final NORGate INSTANCE = new NORGate();
	
	public static final MapCodec<NORGate> CODEC = MapCodec.unit(INSTANCE);
	
	public static final StreamCodec<ByteBuf, NORGate> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	
	private NORGate()
	{
	}
	
	@Override
	public LogicGateType type()
	{
		return LogicGates.NOR;
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
			if(input)
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
