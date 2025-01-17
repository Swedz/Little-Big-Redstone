package net.swedz.little_big_redstone.microchip.logic;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.api.IntRange;
import net.swedz.tesseract.neoforge.api.Assert;

import java.util.List;

public abstract class Logic<L extends Logic>
{
	public static final Codec<Logic> CODEC = LogicTypes.CODEC;
	
	public static final StreamCodec<ByteBuf, Logic> STREAM_CODEC = LogicTypes.STREAM_CODEC;
	
	protected abstract void processTickInternal(LogicContext context, boolean[] inputs);
	
	public final void processTick(LogicContext context, boolean[] inputs)
	{
		int expectedInputs = this.inputs();
		Assert.that(expectedInputs == inputs.length, "Mismatching logic gate input sizes: expected %d but got %d".formatted(expectedInputs, inputs.length));
		this.processTickInternal(context, inputs);
	}
	
	public abstract LogicType<L> type();
	
	public abstract IntRange inputsAllowed();
	
	public abstract int inputs();
	
	public abstract IntRange outputsAllowed();
	
	public abstract int outputs();
	
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
	
	public void resetForPickup()
	{
	}
}
