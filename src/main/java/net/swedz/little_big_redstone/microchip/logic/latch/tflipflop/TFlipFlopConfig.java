package net.swedz.little_big_redstone.microchip.logic.latch.tflipflop;

import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.api.IntRange;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfigMenuBuilder;

import java.util.List;

public final class TFlipFlopConfig extends LogicConfig<TFlipFlopConfig>
{
	public static final TFlipFlopConfig INSTANCE = new TFlipFlopConfig();
	
	private TFlipFlopConfig()
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
	public void loadFrom(TFlipFlopConfig other)
	{
	}
	
	@Override
	public void resetForPickup()
	{
	}
	
	@Override
	public TFlipFlopConfig copy()
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
		return o instanceof TFlipFlopConfig;
	}
}
