package net.swedz.little_big_redstone.microchip.logic;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.LBRText;

import java.util.List;
import java.util.Optional;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public record LogicType<L extends LogicComponent>(
		String id, String englishName, char symbol,
		MapCodec<L> codec, StreamCodec<ByteBuf, L> streamCodec,
		LogicFactory defaultFactory
)
{
	public MutableComponent displayName()
	{
		return Component.translatable(LBR.id(id).toLanguageKey("item"));
	}
	
	public MutableComponent displaySymbol()
	{
		return Component.literal(String.valueOf(symbol)).withStyle(Style.EMPTY.withFont(LBR.id("logic_component")));
	}
	
	public Optional<List<Component>> tooltip(L component, boolean shift, boolean config, boolean configHeader)
	{
		List<Component> lines = Lists.newArrayList();
		
		List<Component> noShiftLines = Lists.newArrayList();
		component.appendNoShiftHoverText(noShiftLines);
		
		if(shift)
		{
			if(!noShiftLines.isEmpty())
			{
				lines.add(Component.empty());
			}
			component.appendShiftHoverText(lines);
		}
		else
		{
			lines.addAll(noShiftLines);
		}
		
		if(config)
		{
			List<Component> configLines = Lists.newArrayList();
			component.config().appendHoverText(configLines);
			if(!configLines.isEmpty())
			{
				if(!lines.isEmpty())
				{
					lines.add(Component.empty());
				}
				if(configHeader)
				{
					lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP));
				}
				lines.addAll(configLines);
			}
		}
		
		return lines.isEmpty() ? Optional.empty() : Optional.of(lines);
	}
	
	public Optional<List<Component>> tooltip(L component, boolean shift, boolean config)
	{
		return this.tooltip(component, shift, config, true);
	}
	
	public Item item()
	{
		return LBRItems.valueOf(id).asItem();
	}
	
	public ItemStack toStack(L component)
	{
		var stack = new ItemStack(this.item());
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
