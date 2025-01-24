package net.swedz.little_big_redstone;

import net.swedz.tesseract.neoforge.tooltip.TranslatableTextEnum;

public enum LBRText implements TranslatableTextEnum
{
	CAPABILITY_ENERGY("Energy"),
	CAPABILITY_FLUID("Fluid"),
	CAPABILITY_ITEM("Item"),
	DIRECTION_DOWN("Down"),
	DIRECTION_EAST("East"),
	DIRECTION_NORTH("North"),
	DIRECTION_SOUTH("South"),
	DIRECTION_UP("Up"),
	DIRECTION_WEST("West"),
	INPUT("Input"),
	LOGIC_CONFIG_BUTTON_LABEL_CANCEL("Cancel"),
	LOGIC_CONFIG_BUTTON_LABEL_DIRECTION("Direction"),
	LOGIC_CONFIG_BUTTON_LABEL_GATE_INPUTS("Inputs: "),
	LOGIC_CONFIG_BUTTON_LABEL_MODE("Mode"),
	LOGIC_CONFIG_BUTTON_LABEL_READER_FILL_MAX("Fill Maximum: "),
	LOGIC_CONFIG_BUTTON_LABEL_READER_FILL_MIN("Fill Minimum: "),
	LOGIC_CONFIG_BUTTON_LABEL_SAVE("Save"),
	LOGIC_CONFIG_BUTTON_LABEL_SEQUENCER_CONTINUOUS("Requires Continuous Power"),
	LOGIC_CONFIG_BUTTON_LABEL_SEQUENCER_DELAY("Delay: "),
	LOGIC_CONFIG_BUTTON_LABEL_SEQUENCER_DURATION("Duration: "),
	LOGIC_CONFIG_BUTTON_TOOLTIP_GATE_INPUTS("The number of inputs that this logic gate can accept."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_IO_DIRECTION("The direction this port should interact with redstone power on."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_IO_MODE("Whether this port should input or output redstone power."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_READER_DIRECTION("The direction this reader should read block capacity from."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_READER_FILL_MAX("The maximum percentage filled the block capacity can be for the output to be on."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_READER_FILL_MIN("The minimum percentage filled the block capacity can be for the output to be on."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_READER_MODE("The type of information to read from the adjacent block."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_CONTINUOUS("Whether the sequencer requires the input to be on to progress the timer."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_DELAY("The time (in ticks) before the output is on."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_DURATION("The time (in ticks) for the output to be on."),
	LOGIC_CONFIG_TOOLTIP("Configuration:"),
	LOGIC_CONFIG_TOOLTIP_DIRECTION("  Direction: %s"),
	LOGIC_CONFIG_TOOLTIP_GATE_INPUTS("  Inputs: %s"),
	LOGIC_CONFIG_TOOLTIP_MODE("  Mode: %s"),
	LOGIC_CONFIG_TOOLTIP_READER_FILL_MAX("  Fill Maximum: %s"),
	LOGIC_CONFIG_TOOLTIP_READER_FILL_MIN("  Fill Minimum: %s"),
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
