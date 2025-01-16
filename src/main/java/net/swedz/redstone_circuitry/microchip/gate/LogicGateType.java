package net.swedz.redstone_circuitry.microchip.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.redstone_circuitry.RCText;

public record LogicGateType<T extends LogicGate>(
		String id, String englishName,
		RCText algebraText,
		MapCodec<T> codec, StreamCodec<ByteBuf, T> streamCodec,
		LogicGateFactory defaultFactory
)
{
	@Override
	public boolean equals(Object o)
	{
		return o instanceof LogicGateType other && id.equals(other.id);
	}
	
	@Override
	public int hashCode()
	{
		return id.hashCode();
	}
}
