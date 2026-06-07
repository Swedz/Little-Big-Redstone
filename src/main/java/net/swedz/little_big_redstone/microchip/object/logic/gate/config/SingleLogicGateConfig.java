package net.swedz.little_big_redstone.microchip.object.logic.gate.config;

import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.tesseract.neoforge.api.range.IntRange;

public interface SingleLogicGateConfig<C extends SingleLogicGateConfig<C>> extends LogicConfig
{
	@Override
	default IntRange inputsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	default int inputs()
	{
		return 1;
	}
	
	@Override
	default IntRange outputsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	default int outputs()
	{
		return 1;
	}
}