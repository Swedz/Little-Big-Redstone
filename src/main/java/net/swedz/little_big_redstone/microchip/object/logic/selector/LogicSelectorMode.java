package net.swedz.little_big_redstone.microchip.object.logic.selector;

import net.swedz.little_big_redstone.LBRText;

public enum LogicSelectorMode
{
	COUNTER(LBRText.LOGIC_CONFIG_SELECTOR_MODE_COUNTER, LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_SELECTOR_MODE_COUNTER),
	SETTER(LBRText.LOGIC_CONFIG_SELECTOR_MODE_SETTER, LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_SELECTOR_MODE_SETTER);
	
	private final LBRText label;
	private final LBRText tooltip;
	
	LogicSelectorMode(LBRText label, LBRText tooltip)
	{
		this.label = label;
		this.tooltip = tooltip;
	}
	
	public LBRText label()
	{
		return label;
	}
	
	public LBRText tooltip()
	{
		return tooltip;
	}
}
