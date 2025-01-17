package net.swedz.redstone_circuitry.microchip.logic;

import net.minecraft.network.chat.Component;
import net.swedz.redstone_circuitry.api.IntRange;
import net.swedz.tesseract.neoforge.api.Assert;

import java.util.List;

public abstract class Logic<L extends Logic>
{
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
