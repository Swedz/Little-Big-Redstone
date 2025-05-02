package net.swedz.little_big_redstone.microchip.logic.sequencer;

import net.swedz.little_big_redstone.LBRText;

public enum LogicSequencerMode
{
	WEAK(LBRText.LOGIC_CONFIG_SEQUENCER_MODE_WEAK, LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_MODE_WEAK),
	STRONG(LBRText.LOGIC_CONFIG_SEQUENCER_MODE_STRONG, LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_MODE_STRONG),
	COUNTER(LBRText.LOGIC_CONFIG_SEQUENCER_MODE_COUNTER, LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_MODE_COUNTER);
	
	private final LBRText label;
	private final LBRText tooltip;
	
	LogicSequencerMode(LBRText label, LBRText tooltip)
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
