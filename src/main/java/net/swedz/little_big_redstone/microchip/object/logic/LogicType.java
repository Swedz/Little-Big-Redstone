package net.swedz.little_big_redstone.microchip.object.logic;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;

import java.util.List;
import java.util.Optional;

public record LogicType<L extends LogicComponent<L, C>, C extends LogicConfig<C>>(
		ResourceLocation id,
		String englishName,
		char symbol,
		
		MapCodec<L> codec,
		StreamCodec<ByteBuf, L> streamCodec,
		LogicFactory<L> defaultFactory,
		
		MapCodec<C> configCodec,
		StreamCodec<ByteBuf, C> configStreamCodec,
		C defaultConfig
)
{
	public <L2 extends LogicComponent<L2, C2>, C2 extends LogicConfig<C2>> boolean is(
			DeferredHolder<LogicType<?, ?>, LogicType<L2, C2>> other
	)
	{
		return id.equals(other.get().id());
	}
	
	public MutableComponent displayName()
	{
		return Component.translatable(id.toLanguageKey("item"));
	}
	
	public MutableComponent displaySymbol()
	{
		return Component.literal(String.valueOf(symbol)).withStyle(Style.EMPTY.withFont(id.withPath("logic_component")));
	}
	
	public Optional<List<Component>> tooltip(C config, boolean holdingShift, boolean includeConfig, boolean configHeader)
	{
		List<Component> lines = Lists.newArrayList();
		
		List<Component> noShiftLines = Lists.newArrayList();
		config.appendNoShiftHoverText(noShiftLines);
		
		if(holdingShift)
		{
			if(!noShiftLines.isEmpty())
			{
				lines.add(Component.empty());
			}
			config.appendShiftHoverText(lines);
		}
		else
		{
			lines.addAll(noShiftLines);
		}
		
		if(includeConfig)
		{
			List<Component> configLines = Lists.newArrayList();
			config.appendConfigHoverText(configLines);
			if(!configLines.isEmpty())
			{
				if(!lines.isEmpty())
				{
					lines.add(Component.empty());
				}
				if(configHeader)
				{
					lines.add(LBR.text().logicConfigTooltip());
				}
				lines.addAll(configLines);
			}
		}
		
		return lines.isEmpty() ? Optional.empty() : Optional.of(lines);
	}
	
	public Optional<List<Component>> tooltip(C config, boolean holdingShift, boolean includeConfig)
	{
		return this.tooltip(config, holdingShift, includeConfig, true);
	}
	
	public Item item()
	{
		return BuiltInRegistries.ITEM.get(id);
	}
	
	public ItemStack toStack(L component)
	{
		var stack = new ItemStack(this.item());
		stack.set(LBRComponents.LOGIC_CONFIG, component.config());
		stack.set(LBRComponents.LOGIC_COLOR, component.color().orElse(null));
		return stack;
	}
	
	public ItemStack toStack()
	{
		return this.toStack(defaultFactory.create());
	}
	
	public L create(C config, Optional<DyeColor> color)
	{
		var logic = defaultFactory.create();
		logic.setConfig(config);
		logic.setColor(color);
		return logic;
	}
	
	public L create(C config, DyeColor color)
	{
		return this.create(config, Optional.ofNullable(color));
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
