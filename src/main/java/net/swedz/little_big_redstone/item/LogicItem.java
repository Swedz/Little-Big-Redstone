package net.swedz.little_big_redstone.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;

import java.util.List;

public final class LogicItem extends Item
{
	private final LogicType<?> type;
	
	public LogicItem(Properties properties, LogicType<?> type)
	{
		super(properties.component(LBRComponents.LOGIC, type.defaultFactory().create()));
		this.type = type;
	}
	
	public LogicType<?> getLogicGateType()
	{
		return type;
	}
	
	private static void appendColorTooltip(List<Component> lines, DyeColor color)
	{
		lines.add(Component.translatable("item.color", Component.translatable("color.minecraft." + color.getName())).withStyle(ChatFormatting.GRAY));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag)
	{
		var component = (LogicComponent<?, ?>) stack.get(LBRComponents.LOGIC);
		component.color().ifPresent((color) -> appendColorTooltip(lines, color));
	}
}
