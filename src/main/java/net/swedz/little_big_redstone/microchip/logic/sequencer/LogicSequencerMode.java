package net.swedz.little_big_redstone.microchip.logic.sequencer;

import net.swedz.little_big_redstone.LBRText;

public enum LogicSequencerMode
{
	WEAK(LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_MODE_WEAK),
	STRONG(LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_MODE_STRONG),
	COUNTER(LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_MODE_COUNTER);
	
	private final LBRText tooltip;
	
	LogicSequencerMode(LBRText tooltip)
	{
		this.tooltip = tooltip;
	}
	
	public LBRText tooltip()
	{
		return tooltip;
	}
}
