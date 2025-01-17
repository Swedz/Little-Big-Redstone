package net.swedz.redstone_circuitry.item;

import net.minecraft.world.item.Item;
import net.swedz.redstone_circuitry.RCComponents;
import net.swedz.redstone_circuitry.microchip.logic.LogicType;

public final class LogicItem extends Item
{
	private final LogicType<?> type;
	
	public LogicItem(Properties properties, LogicType<?> type)
	{
		super(properties.component(RCComponents.LOGIC.get(), type.defaultFactory().create()));
		this.type = type;
	}
	
	public LogicType<?> getLogicGateType()
	{
		return type;
	}
}
