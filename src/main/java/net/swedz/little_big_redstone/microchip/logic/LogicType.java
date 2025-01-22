package net.swedz.little_big_redstone.microchip.logic;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.LBRText;

import java.util.List;
import java.util.Optional;

import static net.swedz.little_big_redstone.LBRTextLine.*;
import static net.swedz.little_big_redstone.LBRTooltips.*;

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
	
	public Optional<List<Component>> tooltip(L component, boolean extra)
	{
		List<Component> lines = Lists.newArrayList();
		
		if(extra)
		{
			component.appendShiftHoverText(lines);
		}
		else
		{
			component.appendNoShiftHoverText(lines);
		}
		
		if(extra)
		{
			List<Component> configLines = Lists.newArrayList();
			component.config().appendHoverText(configLines);
			if(!configLines.isEmpty())
			{
				lines.add(line(LBRText.LOGIC_CONFIGURATION).withStyle(DEFAULT_STYLE));
				lines.addAll(configLines);
			}
		}
		
		return lines.isEmpty() ? Optional.empty() : Optional.of(lines);
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
