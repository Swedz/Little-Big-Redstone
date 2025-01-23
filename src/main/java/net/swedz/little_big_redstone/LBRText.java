package net.swedz.little_big_redstone;

import net.swedz.tesseract.neoforge.tooltip.TranslatableTextEnum;

public enum LBRText implements TranslatableTextEnum
{
	DIRECTION_DOWN("Down"),
	DIRECTION_EAST("East"),
	DIRECTION_NORTH("North"),
	DIRECTION_SOUTH("South"),
	DIRECTION_UP("Up"),
	DIRECTION_WEST("West"),
	INPUT("Input"),
	LOGIC_CONFIG_BUTTON_CANCEL("Cancel"),
	LOGIC_CONFIG_BUTTON_GATE_INPUTS("Inputs: "),
	LOGIC_CONFIG_BUTTON_IO_DIRECTION("Direction"),
	LOGIC_CONFIG_BUTTON_IO_MODE("Mode"),
	LOGIC_CONFIG_BUTTON_SAVE("Save"),
	LOGIC_CONFIG_BUTTON_SEQUENCER_CONTINUOUS("Requires Continuous Power"),
	LOGIC_CONFIG_BUTTON_SEQUENCER_DELAY("Delay: "),
	LOGIC_CONFIG_BUTTON_SEQUENCER_DURATION("Duration: "),
	LOGIC_CONFIG_TOOLTIP("Configuration:"),
	LOGIC_CONFIG_TOOLTIP_GATE_INPUTS("  Inputs: %s"),
	LOGIC_CONFIG_TOOLTIP_IO_DIRECTION("  Direction: %s"),
	LOGIC_CONFIG_TOOLTIP_IO_MODE("  Mode: %s"),
	LOGIC_CONFIG_TOOLTIP_SEQUENCER_CONTINUOUS("  Continuous: %s"),
	LOGIC_CONFIG_TOOLTIP_SEQUENCER_DELAY("  Delay: %d"),
	LOGIC_CONFIG_TOOLTIP_SEQUENCER_DURATION("  Duration: %d"),
	LOGIC_GATE_ALGEBRA("Q = %s"),
	LOGIC_GATE_ALGEBRA_AND("A \u2227 B"),
	LOGIC_GATE_ALGEBRA_NAND("A \u2191 B"),
	LOGIC_GATE_ALGEBRA_NOR("A \u2193 B"),
	LOGIC_GATE_ALGEBRA_NOT("\u00ACA"),
	LOGIC_GATE_ALGEBRA_OR("A \u2228 B"),
	LOGIC_GATE_ALGEBRA_XOR("A \u22BB B"),
	NO("No"),
	OUTPUT("Output"),
	YES("Yes");
	
	private final String englishText;
	
	LBRText(String englishText)
	{
		this.englishText = englishText;
	}
	
	@Override
	public String englishText()
	{
		return englishText;
	}
	
	@Override
	public String getTranslationKey()
	{
		return "text.%s.%s".formatted(LBR.ID, this.name().toLowerCase());
	}
}
