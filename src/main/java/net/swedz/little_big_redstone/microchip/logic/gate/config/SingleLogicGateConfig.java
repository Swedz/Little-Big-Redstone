package net.swedz.little_big_redstone.microchip.logic.gate.config;

import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.api.IntRange;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfigMenuBuilder;

import java.util.List;

public final class SingleLogicGateConfig extends LogicConfig<SingleLogicGateConfig>
{
	public static final SingleLogicGateConfig INSTANCE = new SingleLogicGateConfig();
	
	private SingleLogicGateConfig()
	{
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public int inputs()
	{
		return 1;
	}
	
	@Override
	public IntRange outputsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public int outputs()
	{
		return 1;
	}
	
	@Override
	public void appendHoverText(List<Component> lines)
	{
	}
	
	@Override
	public void buildMenu(LogicConfigMenuBuilder builder)
	{
	}
	
	@Override
	public void loadFrom(SingleLogicGateConfig other)
	{
	}
	
	@Override
	public SingleLogicGateConfig copy()
	{
		return this;
	}
	
	@Override
	public int hashCode()
	{
		return 0;
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof SingleLogicGateConfig other;
	}
}
