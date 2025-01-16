package net.swedz.redstone_circuitry.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.swedz.redstone_circuitry.RCComponents;
import net.swedz.redstone_circuitry.microchip.gate.LogicGateType;

import java.util.List;

public final class LogicGateItem extends Item
{
	private final LogicGateType<?> type;
	
	public LogicGateItem(Properties properties, LogicGateType<?> type)
	{
		super(properties.component(RCComponents.LOGIC_GATE.get(), type.defaultFactory().create()));
		this.type = type;
	}
	
	public LogicGateType<?> getLogicGateType()
	{
		return type;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag tooltipFlag)
	{
		stack.get(RCComponents.LOGIC_GATE.get()).appendTooltip(context, lines);
	}
}
