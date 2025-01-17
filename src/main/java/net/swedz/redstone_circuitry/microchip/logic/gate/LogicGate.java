package net.swedz.redstone_circuitry.microchip.logic.gate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.swedz.redstone_circuitry.api.IntRange;
import net.swedz.redstone_circuitry.microchip.logic.Logic;
import net.swedz.tesseract.neoforge.api.Assert;

public abstract class LogicGate<G extends LogicGate> extends Logic<G>
{
	@Override
	public final IntRange outputs()
	{
		return new IntRange(1, 1);
	}
	
	protected abstract boolean processInputs(Level level, BlockPos pos, boolean[] inputs);
	
	public final boolean process(Level level, BlockPos pos, boolean[] inputs)
	{
		Assert.that(this.inputs().contains(inputs.length));
		return this.processInputs(level, pos, inputs);
	}
}
