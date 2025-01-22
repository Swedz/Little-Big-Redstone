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
	LOGIC_CONFIGURATION("Configuration:"),
	LOGIC_CONFIGURATION_GATE_INPUTS("  Inputs: %s"),
	LOGIC_CONFIGURATION_IO_DIRECTION("  Direction: %s"),
	LOGIC_CONFIGURATION_IO_MODE("  Mode: %s"),
	LOGIC_CONFIGURATION_SEQUENCER_CONTINUOUS("  Continuous: %s"),
	LOGIC_CONFIGURATION_SEQUENCER_DELAY("  Delay: %d"),
	LOGIC_CONFIGURATION_SEQUENCER_DURATION("  Duration: %d"),
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
