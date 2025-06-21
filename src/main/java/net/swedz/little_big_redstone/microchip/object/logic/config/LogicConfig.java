package net.swedz.little_big_redstone.microchip.object.logic.config;

import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponents;
import net.swedz.little_big_redstone.microchip.object.logic.LogicPortHolder;

import java.util.Arrays;
import java.util.List;

public abstract class LogicConfig<C extends LogicConfig<C>> implements LogicPortHolder
{
	protected boolean valid = true;
	
	/**
	 * Used to lock signals on or off in the guide.
	 * <br><br>
	 * <b>WARNING: Should not be used at all outside of the guide. Ever. If this is not null for a logic component
	 * outside of the guide, something has gone terribly wrong.</b>
	 */
	protected Boolean[] outputLocks;
	
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
	
	public final void setOutputLock(int index, Boolean lock)
	{
		if(outputLocks == null || outputLocks.length <= index)
		{
			outputLocks = outputLocks == null ? new Boolean[index + 1] : Arrays.copyOf(outputLocks, index + 1);
		}
		outputLocks[index] = lock;
	}
	
	public final Boolean getOutputLock(int index)
	{
		return (outputLocks == null || outputLocks.length <= index) ? null : outputLocks[index];
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
