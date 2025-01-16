package net.swedz.redstone_circuitry.item;

import net.minecraft.world.item.Item;
import net.swedz.redstone_circuitry.RCComponents;
import net.swedz.redstone_circuitry.microchip.gate.LogicGateType;

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
}
