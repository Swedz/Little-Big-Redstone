package net.swedz.little_big_redstone.microchip.logic;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.api.IntRange;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfig;
import net.swedz.tesseract.neoforge.api.Assert;

import java.util.List;

public abstract class LogicComponent<L extends LogicComponent<L, C>, C extends LogicConfig> implements LogicPortHolder
{
	public static final Codec<LogicComponent> CODEC = LogicTypes.CODEC;
	
	public static final StreamCodec<ByteBuf, LogicComponent> STREAM_CODEC = LogicTypes.STREAM_CODEC;
	
	protected final C config;
	
	protected LogicComponent(C config)
	{
		this.config = config;
	}
	
	protected LogicComponent()
	{
		this.config = this.defaultConfig();
	}
	
	public C config()
	{
		return config;
	}
	
	protected abstract C defaultConfig();
	
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
	
	protected abstract void processTickInternal(LogicContext context, boolean[] inputs);
	
	public final void processTick(LogicContext context, boolean[] inputs)
	{
		int expectedInputs = this.inputs();
		Assert.that(expectedInputs == inputs.length, "Mismatching logic component input sizes: expected %d but got %d".formatted(expectedInputs, inputs.length));
		this.processTickInternal(context, inputs);
	}
	
	public abstract LogicType<L> type();
	
	public abstract boolean output(int index);
	
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
		this.internalLoadFrom(other);
	}
	
	public void resetForPickup()
	{
	}
	
	public abstract L copy();
	
	public abstract int hashCode();
	
	public abstract boolean equals(Object o);
}
