package net.swedz.redstone_circuitry.microchip.gate.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.swedz.redstone_circuitry.microchip.gate.LogicGate;
import net.swedz.redstone_circuitry.microchip.gate.LogicGateType;
import net.swedz.redstone_circuitry.microchip.gate.LogicGates;

public final class NOTGate implements LogicGate
{
	public static final NOTGate INSTANCE = new NOTGate();
	
	public static final MapCodec<NOTGate> CODEC = MapCodec.unit(INSTANCE);
	
	public static final StreamCodec<ByteBuf, NOTGate> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	
	private NOTGate()
	{
	}
	
	@Override
	public LogicGateType type()
	{
		return LogicGates.NOT;
	}
	
	@Override
	public int inputCount()
	{
		return 1;
	}
	
	@Override
	public boolean process(Level level, BlockPos pos, boolean[] inputs)
	{
		return !inputs[0];
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
