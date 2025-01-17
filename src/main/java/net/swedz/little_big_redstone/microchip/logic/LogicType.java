package net.swedz.little_big_redstone.microchip.logic;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record LogicType<L extends Logic>(
		String id, String englishName,
		MapCodec<L> codec, StreamCodec<ByteBuf, L> streamCodec,
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
