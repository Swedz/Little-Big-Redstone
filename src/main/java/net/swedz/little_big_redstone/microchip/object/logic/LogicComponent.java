package net.swedz.little_big_redstone.microchip.object.logic;

import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.tesseract.api.Assert;
import net.swedz.tesseract.neoforge.api.range.IntRange;

import java.util.Arrays;
import java.util.Optional;

public abstract class LogicComponent<L extends LogicComponent<L, C>, C extends LogicConfig> implements LogicPortHolder
{
	protected C config;
	
	private Boolean configValid;
	
	protected Optional<DyeColor> color;
	
	/**
	 * Used to lock signals on or off in the guide.
	 * <br><br>
	 * <b>WARNING: Should not be used at all outside of the guide. Ever. If this is not null for a logic component
	 * outside of the guide, something has gone terribly wrong.</b>
	 */
	protected Integer[] outputLocks;
	
	protected LogicComponent(C config, Optional<DyeColor> color)
	{
		this.config = config;
		this.color = color;
	}
	
	protected LogicComponent(Optional<DyeColor> color)
	{
		this.color = color;
		this.config = (C) this.type().defaultConfig();
	}
	
	public final C config()
	{
		return config;
	}
	
	public final void setConfig(C config)
	{
		this.config = config;
		configValid = null;
	}
	
	public final boolean isConfigValid()
	{
		return configValid == null || configValid;
	}
	
	public final void updateConfigValidState(LogicComponents components)
	{
		configValid = config.checkValid(components);
	}
	
	public final Optional<DyeColor> color()
	{
		return color;
	}
	
	public final void setColor(Optional<DyeColor> color)
	{
		this.color = color;
	}
	
	public final void resetColor()
	{
		color = Optional.empty();
	}
	
	public abstract LogicType type();
	
	@Override
	public final IntRange inputPortsAllowed()
	{
		return config.inputPortsAllowed();
	}
	
	@Override
	public final int inputPorts()
	{
		return config.inputPorts();
	}
	
	@Override
	public final IntRange outputPortsAllowed()
	{
		return config.outputPortsAllowed();
	}
	
	@Override
	public final int outputPorts()
	{
		return config.outputPorts();
	}
	
	@Override
	public final LogicGridSize size()
	{
		return config.size();
	}
	
	protected abstract void processTickInternal(LogicTickingContext context, int[] inputs);
	
	public final void processTick(LogicTickingContext context, int[] inputs)
	{
		int expectedInputs = this.inputPorts();
		Assert.that(expectedInputs == inputs.length, "Mismatching logic component input sizes: expected %d but got %d".formatted(expectedInputs, inputs.length));
		if(configValid == null)
		{
			configValid = context.checkValid(config);
		}
		if(this.isConfigValid())
		{
			this.processTickInternal(context, inputs);
		}
	}
	
	protected abstract int outputInternal(int index);
	
	public final void setOutputLock(int index, Integer lock)
	{
		if(outputLocks == null || outputLocks.length <= index)
		{
			outputLocks = outputLocks == null ? new Integer[index + 1] : Arrays.copyOf(outputLocks, index + 1);
		}
		outputLocks[index] = lock;
	}
	
	private Integer getOutputLock(int index)
	{
		return (outputLocks == null || outputLocks.length <= index) ? null : outputLocks[index];
	}
	
	public final int output(int index)
	{
		var lock = this.getOutputLock(index);
		if(lock != null)
		{
			return lock;
		}
		return this.outputInternal(index);
	}
	
	protected abstract void internalLoadFrom(L other);
	
	public final void loadFrom(L other)
	{
		this.setConfig(other.config());
		color = other.color();
		this.internalLoadFrom(other);
	}
	
	public final L copy()
	{
		var copy = (L) this.type().defaultFactory().create();
		copy.loadFrom((L) this);
		return copy;
	}
	
	public abstract int hashCode();
	
	public abstract boolean equals(Object o);
}