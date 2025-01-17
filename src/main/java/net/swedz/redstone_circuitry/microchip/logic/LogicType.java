package net.swedz.redstone_circuitry.microchip.logic;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record LogicType<G extends Logic>(
		String id, String englishName,
		MapCodec<G> codec, StreamCodec<ByteBuf, G> streamCodec,
		LogicFactory defaultFactory
)
{
	@Override
	public boolean equals(Object o)
	{
		return o instanceof LogicType other && id.equals(other.id);
	}
	
	@Override
	public int hashCode()
	{
		return id.hashCode();
	}
}
