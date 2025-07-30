package net.swedz.little_big_redstone.microchip.object.logic.io;

import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.gui.logicconfig.button.IconCycleButtonIcon;

public enum LogicIOComparisonMode implements IconCycleButtonIcon
{
	LESS_THAN_OR_EQUAL_TO(
			"\u2264", LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_IO_COMPARISON_MODE_LESS_THAN_OR_EQUAL_TO,
			36, 0,
			(input, setting) -> input <= setting
	),
	EQUAL_TO(
			"=", LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_IO_COMPARISON_MODE_EQUAL_TO,
			36 + 18, 0,
			(input, setting) -> input == setting
	),
	GREATER_THAN_OR_EQUAL_TO(
			"\u2265", LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_IO_COMPARISON_MODE_GREATER_THAN_OR_EQUAL_TO,
			36 + (18 * 2), 0,
			(input, setting) -> input >= setting
	);
	
	private final String display;
	private final LBRText tooltipButton;
	
	private final int u, v;
	
	private final Test test;
	
	LogicIOComparisonMode(String display, LBRText tooltipButton, int u, int v, Test test)
	{
		this.display = display;
		this.tooltipButton = tooltipButton;
		this.u = u;
		this.v = v;
		this.test = test;
	}
	
	public String display()
	{
		return display;
	}
	
	public LBRText tooltipButton()
	{
		return tooltipButton;
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
	
	public boolean test(int input, int setting)
	{
		return test.test(input, setting);
	}
	
	private interface Test
	{
		boolean test(int input, int setting);
	}
}
