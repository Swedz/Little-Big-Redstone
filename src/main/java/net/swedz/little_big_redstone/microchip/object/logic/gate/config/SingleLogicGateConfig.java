package net.swedz.little_big_redstone.microchip.object.logic.gate.config;

import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.tesseract.neoforge.api.range.IntRange;

public interface SingleLogicGateConfig<C extends SingleLogicGateConfig<C>> extends LogicConfig
{
	@Override
	default IntRange inputPortsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	default int inputPorts()
	{
		return 1;
	}
	
	@Override
	default IntRange outputPortsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	default int outputPorts()
	{
		return 1;
	}
}