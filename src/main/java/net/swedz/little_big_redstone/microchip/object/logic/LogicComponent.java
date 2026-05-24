package net.swedz.little_big_redstone.microchip.object.logic;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.tesseract.api.Assert;
import net.swedz.tesseract.neoforge.api.range.IntRange;

import java.util.List;
import java.util.Optional;

public abstract class LogicComponent<L extends LogicComponent<L, C>, C extends LogicConfig> implements LogicPortHolder
{
	public static final Codec<LogicComponent> CODEC = LogicTypes.CODEC;
	
	public static final StreamCodec<ByteBuf, LogicComponent> STREAM_CODEC = LogicTypes.STREAM_CODEC;
	
	protected final C config;
	
	protected Optional<DyeColor> color;
	
	protected LogicComponent(C config, Optional<DyeColor> color)
	{
		this.config = config;
		this.color = color;
	}
	
	protected LogicComponent(Optional<DyeColor> color)
	{
		this.color = color;
		this.config = this.defaultConfig();
	}
	
	public C config()
	{
		return config;
	}
	
	public void resetConfig()
	{
		config.loadFrom(this.defaultConfig());
	}
	
	protected abstract C defaultConfig();
	
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
	
	public abstract LogicType<L> type();
	
	@Override
	public final IntRange inputsAllowed()
	{
		return config.inputsAllowed();
	}
	
	@Override
	public final int inputs()
	{
		return config.inputs();
	}
	
	@Override
	public final IntRange outputsAllowed()
	{
		return config.outputsAllowed();
	}
	
	@Override
	public final int outputs()
	{
		return config.outputs();
	}
	
	protected abstract void processTickInternal(LogicContext context, int[] inputs);
	
	public final void processTick(LogicContext context, int[] inputs)
	{
		int expectedInputs = this.inputs();
		Assert.that(expectedInputs == inputs.length, "Mismatching logic component input sizes: expected %d but got %d".formatted(expectedInputs, inputs.length));
		if(config.isValid())
		{
			this.processTickInternal(context, inputs);
		}
	}
	
	protected abstract int outputInternal(int index);
	
	public final int output(int index)
	{
		var lock = config.getOutputLock(index);
		if(lock != null)
		{
			return lock;
		}
		return this.outputInternal(index);
	}
	
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
	
	protected abstract void internalLoadFrom(L other);
	
	public final void loadFrom(L other)
	{
		config.loadFrom(other.config);
		color = other.color;
		this.internalLoadFrom(other);
	}
	
	protected abstract void internalResetForPickup();
	
	public final void resetForPickup()
	{
		config.resetForPickup();
		this.internalResetForPickup();
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