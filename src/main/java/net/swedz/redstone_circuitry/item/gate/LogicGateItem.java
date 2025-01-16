package net.swedz.redstone_circuitry.item.gate;

import net.minecraft.world.item.Item;

public final class LogicGateItem extends Item
{
	private final LogicGate gate;
	
	public LogicGateItem(Properties properties, LogicGate gate)
	{
		super(properties);
		this.gate = gate;
	}
	
	public LogicGate getLogicGate()
	{
		return gate;
	}
}
