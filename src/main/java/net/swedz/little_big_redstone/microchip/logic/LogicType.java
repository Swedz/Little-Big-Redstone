package net.swedz.little_big_redstone.microchip.logic;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;

public record LogicType<L extends LogicComponent>(
		String id, String englishName,
		MapCodec<L> codec, StreamCodec<ByteBuf, L> streamCodec,
		LogicFactory defaultFactory
)
{
	public MutableComponent displayName()
	{
		return Component.translatable(LBR.id(id).toLanguageKey("item"));
	}
	
	public ItemStack toStack(L component)
	{
		var stack = new ItemStack(LBRItems.valueOf(id));
		var copy = component.copy();
		copy.resetForPickup();
		stack.set(LBRComponents.LOGIC, copy);
		return stack;
	}
	
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
