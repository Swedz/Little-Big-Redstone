package net.swedz.little_big_redstone.microchip.object.logic.config;

import net.swedz.little_big_redstone.gui.logicconfig.button.iconcycle.IconCycleLogicConfigButtonIcon;

public enum LogicComparisonMode implements IconCycleLogicConfigButtonIcon
{
	LESS_THAN_OR_EQUAL_TO("\u2264", 36, 0, (input, setting) -> input <= setting),
	EQUAL_TO("=", 36 + 18, 0, (input, setting) -> input == setting),
	GREATER_THAN_OR_EQUAL_TO("\u2265", 36 + (18 * 2), 0, (input, setting) -> input >= setting);
	
	private final String symbol;
	
	private final int u, v;
	
	private final Test test;
	
	LogicComparisonMode(String symbol, int u, int v, Test test)
	{
		this.symbol = symbol;
		this.u = u;
		this.v = v;
		this.test = test;
	}
	
	public String symbol()
	{
		return symbol;
	}
	
	@Override
	public int u()
	{
		return u;
	}
	
	@Override
	public int v()
	{
		return v;
	}
	
	public boolean test(Number input, Number setting)
	{
		return test.test(input.floatValue(), setting.floatValue());
	}
	
	private interface Test
	{
		boolean test(float input, float setting);
	}
}
