package net.swedz.little_big_redstone.microchip.logic.config;

import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.microchip.logic.LogicComponents;
import net.swedz.little_big_redstone.microchip.logic.LogicPortHolder;

import java.util.List;

public abstract class LogicConfig<C extends LogicConfig<C>> implements LogicPortHolder
{
	protected boolean valid = true;
	
	public final boolean isValid()
	{
		return valid;
	}
	
	public final void recalculateValidity(LogicComponents components)
	{
		valid = this.calculateValidity(components);
	}
	
	protected boolean calculateValidity(LogicComponents components)
	{
		return true;
	}
	
	public void appendHoverText(List<Component> lines)
	{
	}
	
	public boolean hasMenu()
	{
		return false;
	}
	
	public void buildMenu(LogicConfigMenuBuilder builder)
	{
	}
	
	public abstract void loadFrom(C other);
	
	public abstract void resetForPickup();
	
	public abstract C copy();
	
	public abstract int hashCode();
	
	public abstract boolean equals(Object o);
}
