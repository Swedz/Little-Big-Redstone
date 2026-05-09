package net.swedz.little_big_redstone.microchip.object.logic.config;

import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponents;
import net.swedz.little_big_redstone.microchip.object.logic.LogicPortHolder;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;

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
	protected Integer[] outputLocks;
	
	/**
	 * Used to hide logic in the guide so that only the wires to/from it render.
	 * <br><br>
	 * <b>WARNING: Should not be used at all outside the guide. Ever. If this is not false for a logic component
	 * outside of the guide, something has gone terribly wrong.</b>
	 */
	protected boolean hidden = false;
	
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
	
	public final void setOutputLock(int index, Integer lock)
	{
		if(outputLocks == null || outputLocks.length <= index)
		{
			outputLocks = outputLocks == null ? new Integer[index + 1] : Arrays.copyOf(outputLocks, index + 1);
		}
		outputLocks[index] = lock;
	}
	
	public final Integer getOutputLock(int index)
	{
		return (outputLocks == null || outputLocks.length <= index) ? null : outputLocks[index];
	}
	
	public final void hide()
	{
		hidden = true;
	}
	
	public final boolean isVisible()
	{
		return !hidden;
	}
	
	public void appendHoverText(List<Component> lines)
	{
	}
	
	public boolean hasMenu()
	{
		return false;
	}
	
	public LogicConfigMenuProvider<C> getMenuProvider()
	{
		return new LogicConfigMenuProvider<>((C) this)
		{
			@Override
			public void create(LogicConfigMenuBuilder builder, int width, int height)
			{
			}
		};
	}
	
	protected abstract void internalLoadFrom(C other);
	
	public final void loadFrom(C other)
	{
		valid = other.valid;
		outputLocks = other.outputLocks;
		hidden = other.hidden;
		this.internalLoadFrom(other);
	}
	
	public abstract void resetForPickup();
	
	public abstract int hashCode();
	
	public abstract boolean equals(Object o);
}