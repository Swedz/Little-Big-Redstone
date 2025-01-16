package net.swedz.redstone_circuitry.microchip.gate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface LogicGate<T extends LogicGate>
{
	LogicGateType<T> type();
	
	int inputCount();
	
	boolean process(Level level, BlockPos pos, boolean[] inputs);
}
