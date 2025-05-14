package net.swedz.little_big_redstone.item;

import net.minecraft.world.item.Item;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;

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
}
