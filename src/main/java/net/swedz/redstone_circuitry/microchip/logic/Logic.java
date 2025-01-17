package net.swedz.redstone_circuitry.microchip.logic;

import net.minecraft.network.chat.Component;
import net.swedz.redstone_circuitry.api.IntRange;

import java.util.List;

public abstract class Logic<G extends Logic>
{
	public abstract LogicType<G> type();
	
	public abstract IntRange inputs();
	
	public abstract IntRange outputs();
	
	public LogicGridSize size()
	{
		return new LogicGridSize(1, 1);
	}
	
	public void appendNoShiftHoverText(List<Component> lines)
	{
	}
	
	public void appendShiftHoverText(List<Component> lines)
	{
	}
}
