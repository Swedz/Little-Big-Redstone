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
	LOGIC_CONFIG_BUTTON_LABEL_DURATION("Duration: "),
	LOGIC_CONFIG_BUTTON_LABEL_INPUTS("Inputs: "),
	LOGIC_CONFIG_BUTTON_LABEL_IO_SIGNAL_STRENGTH("Signal Strength: "),
	LOGIC_CONFIG_BUTTON_LABEL_MODE("Mode"),
	LOGIC_CONFIG_BUTTON_LABEL_OUTPUTS("Outputs: "),
	LOGIC_CONFIG_BUTTON_LABEL_READER_FILL_THRESHOLD("Fill Threshold: "),
	LOGIC_CONFIG_BUTTON_LABEL_SAVE("Save"),
	LOGIC_CONFIG_BUTTON_LABEL_SEQUENCER_AUTO_RESET("Auto Reset"),
	LOGIC_CONFIG_BUTTON_LABEL_SEQUENCER_DELAY("Delay: "),
	LOGIC_CONFIG_BUTTON_LABEL_SEQUENCER_RESET_PORT("Reset Port"),
	LOGIC_CONFIG_BUTTON_TOOLTIP_DURATION("The time (in ticks) for the output to be on."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_INPUTS("The number of inputs that this component can accept."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_IO_DIRECTION("The direction this port should interact with redstone power on."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_IO_MODE("Whether this port should input or output redstone power."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_IO_SIGNAL_STRENGTH("When in input mode, the minimum redstone signal strength required for the output to be on." +
												   "\nWhen in output mode, the redstone signal strength that will be outputted."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_OUTPUTS("The number of outputs that this component can yield."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_READER_DIRECTION("The direction this reader should read block capacity from."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_READER_FILL_THRESHOLD("The minimum percentage filled the block capacity must be for the output to be on."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_READER_MODE("The type of information to read from the adjacent block."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_SELECTOR_MODE_COUNTER("When the first input is ON, the selected output will move up. When the second input is ON, the selected output will move down."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_SELECTOR_MODE_SETTER("The lowest output with a corresponding ON input will have an ON output."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_AUTO_RESET("Whether the sequencer should automatically reset its progress immediately after yielding an output of ON."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_DELAY("The time (in ticks) before the output is on."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_MODE_COUNTER("While input is ON, the sequencer will increment until it has met X ticks and then emit an output of ON."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_MODE_STRONG("While input is ON, the sequencer will increment until it has met X ticks and then emit an output of ON. While input is OFF, the sequencer will decrement."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_MODE_WEAK("After an ON input signal, the sequencer will wait X ticks and then emit an output of ON."),
	LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_RESET_PORT("Whether a second wire port should be added that will forcefully reset the sequencer's progress."),
	LOGIC_CONFIG_SELECTOR_MODE_COUNTER("Counter"),
	LOGIC_CONFIG_SELECTOR_MODE_SETTER("Setter"),
	LOGIC_CONFIG_SEQUENCER_MODE_COUNTER("Counter"),
	LOGIC_CONFIG_SEQUENCER_MODE_STRONG("Strong"),
	LOGIC_CONFIG_SEQUENCER_MODE_WEAK("Weak"),
	LOGIC_CONFIG_TOOLTIP("Configuration:"),
	LOGIC_CONFIG_TOOLTIP_DIRECTION("  Direction: %s"),
	LOGIC_CONFIG_TOOLTIP_DURATION("  Duration: %d"),
	LOGIC_CONFIG_TOOLTIP_INPUTS("  Inputs: %s"),
	LOGIC_CONFIG_TOOLTIP_IO_SIGNAL_STRENGTH("  Signal Strength: %d"),
	LOGIC_CONFIG_TOOLTIP_MODE("  Mode: %s"),
	LOGIC_CONFIG_TOOLTIP_OUTPUTS("  Outputs: %s"),
	LOGIC_CONFIG_TOOLTIP_READER_FILL_MAX("  Fill Maximum: %s"),
	LOGIC_CONFIG_TOOLTIP_READER_FILL_MIN("  Fill Minimum: %s"),
	LOGIC_CONFIG_TOOLTIP_SEQUENCER_AUTO_RESET("  Auto Reset: %s"),
	LOGIC_CONFIG_TOOLTIP_SEQUENCER_DELAY("  Delay: %d"),
	LOGIC_CONFIG_TOOLTIP_SEQUENCER_RESET_PORT("  Reset Port: %s"),
	LOGIC_GATE_ALGEBRA("Q = %s"),
	LOGIC_GATE_ALGEBRA_AND("A \u2227 B"),
	LOGIC_GATE_ALGEBRA_NAND("A \u2191 B"),
	LOGIC_GATE_ALGEBRA_NOR("A \u2193 B"),
	LOGIC_GATE_ALGEBRA_NOT("\u00ACA"),
	LOGIC_GATE_ALGEBRA_OR("A \u2228 B"),
	LOGIC_GATE_ALGEBRA_XOR("A \u22BB B"),
	LOGIC_HELP_AND_GATE("Output is ON when all inputs are ON, otherwise output is OFF."),
	LOGIC_HELP_IO_PORT_1("Can either input or output a redstone signal in the world on a single face. Multiple I/O ports can be used to input and output from different faces."),
	LOGIC_HELP_IO_PORT_2("A microchip cannot have both an input and output port on the same face."),
	LOGIC_HELP_NAND_GATE("Output is OFF when all inputs are ON, otherwise output is ON."),
	LOGIC_HELP_NOR_GATE("Output is ON when all inputs are OFF, otherwise output is ON."),
	LOGIC_HELP_NOT_GATE("Output is ON when the input is OFF, and output is OFF when the input is ON."),
	LOGIC_HELP_OR_GATE("Output is ON when any input is ON, otherwise output is OFF."),
	LOGIC_HELP_PULSE_THROTTLER("Throttles an input signal and yields an output with a configurable duration."),
	LOGIC_HELP_READER_1("Checks the filled percentage of an adjacent block and yields an ON signal if the block's filled percentage is greater than or equal to the set fill threshold."),
	LOGIC_HELP_READER_2("Can be used on item, fluid, or energy storages."),
	LOGIC_HELP_RS_NOR_LATCH_1("When the reset (R) input is ON, the output is always OFF."),
	LOGIC_HELP_RS_NOR_LATCH_2("When the set (S) input is ON and the reset (R) input is OFF, the output is set to ON and will remain ON until the reset (R) input is ON."),
	LOGIC_HELP_SELECTOR("Emits a single ON output and will switch to adjacent ports in a strategy depending on the mode selected."),
	LOGIC_HELP_SEQUENCER_1("Delays the output signal by the set delay (in ticks)."),
	LOGIC_HELP_SEQUENCER_2("The mode determines the behavior of the sequencer and can be set to Weak, Strong, or Counter."),
	LOGIC_HELP_SEQUENCER_3("By default the sequencer will not reset after yielding an output state. If auto reset is enabled, the progress will be reset immediately after yielding an output of ON. Additionally, if reset port is enabled, a second input port will be available. When the second input port is given an ON input, the progress of the sequencer will be reset. As long as an ON input is present on this input, the sequencer cannot progress."),
	LOGIC_HELP_T_FLIP_FLOP("When the input goes from OFF to ON, the output signal swaps states."),
	LOGIC_HELP_XOR_GATE("Output is ON when the amount of ON inputs is odd, otherwise output is OFF."),
	NO("No"),
	OUTPUT("Output"),
	STICKY_NOTE("Sticky Note"),
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
