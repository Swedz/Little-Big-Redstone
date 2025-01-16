package net.swedz.redstone_circuitry.microchip.gate;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.List;

public interface LogicGate<T extends LogicGate>
{
	LogicGateType<T> type();
	
	int inputCount();
	
	boolean process(Level level, BlockPos pos, boolean[] inputs);
	
	default void appendNoShiftHoverText(List<Component> lines)
	{
	}
	
	default void appendShiftHoverText(List<Component> lines)
	{
	}
}
