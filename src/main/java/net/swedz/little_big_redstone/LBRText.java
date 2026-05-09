package net.swedz.little_big_redstone;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicMode;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicComparisonMode;
import net.swedz.tesseract.neoforge.lang.annotation.LangKey;
import net.swedz.tesseract.neoforge.lang.annotation.Parsed;
import net.swedz.tesseract.neoforge.lang.annotation.WithStyle;

public interface LBRText
{
	@LangKey(text = "Comparator")
	MutableComponent capabilityComparator();
	
	@LangKey(text = "Energy")
	MutableComponent capabilityEnergy();
	
	@LangKey(text = "Fluid")
	MutableComponent capabilityFluid();
	
	@LangKey(text = "Item")
	MutableComponent capabilityItem();
	
	@LangKey(text = "Down")
	@WithStyle("direction.down")
	MutableComponent directionDown();
	
	@LangKey(text = "East")
	@WithStyle("direction.east")
	MutableComponent directionEast();
	
	@LangKey(text = "North")
	@WithStyle("direction.north")
	MutableComponent directionNorth();
	
	@LangKey(text = "South")
	@WithStyle("direction.south")
	MutableComponent directionSouth();
	
	@LangKey(text = "Up")
	@WithStyle("direction.up")
	MutableComponent directionUp();
	
	@LangKey(text = "West")
	@WithStyle("direction.west")
	MutableComponent directionWest();
	
	@LangKey(text = "Floppy Disk")
	MutableComponent floppyDisk();
	
	@LangKey(text = "Failed to install microchip program.")
	MutableComponent floppyDiskApplyFailure();
	
	@LangKey(text = "Installed microchip program to the microchip from the floppy disk.")
	MutableComponent floppyDiskApplySuccess();
	
	@LangKey(text = "Close")
	MutableComponent floppyDiskButtonClose();
	
	@LangKey(text = "Load")
	MutableComponent floppyDiskButtonLoad();
	
	@LangKey(text = "Save")
	MutableComponent floppyDiskButtonSave();
	
	@LangKey(text = "Cleared floppy disk microchip program.")
	MutableComponent floppyDiskClear();
	
	@LangKey(text = "No microchip program file exists for the name %s.")
	@WithStyle("red")
	MutableComponent floppyDiskFileDoesntExist(String name);
	
	@LangKey(text = "There was an error while trying to load the microchip program from a file. See logs for more information.")
	@WithStyle("red")
	MutableComponent floppyDiskFileFailedToLoad();
	
	@LangKey(text = "There was an error while trying to save the floppy disk's contents to a file. See logs for more information.")
	@WithStyle("red")
	MutableComponent floppyDiskFileFailedToSave();
	
	@LangKey(text = "Loaded the microchip program %s!")
	@WithStyle("green")
	MutableComponent floppyDiskFileLoaded(String name);
	
	@LangKey(text = "Saved the floppy disk's contents as %s!")
	@WithStyle("green")
	MutableComponent floppyDiskFileSaved(String name);
	
	@LangKey(text = "Program: %s")
	@WithStyle("tooltip")
	MutableComponent floppyDiskProgramName(
			@WithStyle("white") String name
	);
	
	@LangKey(text = "Can save, copy & paste Microchip programs.")
	@WithStyle("tooltip")
	MutableComponent floppyDiskHelp1();
	
	@LangKey(text = "Press %s + %s on a Microchip to save it to the disk.")
	@WithStyle("tooltip")
	MutableComponent floppyDiskHelp2(
			@Parsed("keybind") @WithStyle("highlighted") String keybind1,
			@Parsed("keybind") @WithStyle("highlighted") String keybind2
	);
	
	@LangKey(text = "Use %s on a Microchip to install the program. This does require that you have all the components and wires needed available in your inventory.")
	@WithStyle("tooltip")
	MutableComponent floppyDiskHelp3(@Parsed("keybind") @WithStyle("highlighted") String keybind);
	
	@LangKey(text = "Use %s to open the menu to save or load a program to or from a local file.")
	@WithStyle("tooltip")
	MutableComponent floppyDiskHelp4(@Parsed("keybind") @WithStyle("highlighted") String keybind);
	
	@LangKey(text = "Program Name")
	MutableComponent floppyDiskInputProgramName();
	
	@LangKey(text = "+%s")
	MutableComponent floppyDiskMoreItems(int count);
	
	@LangKey(text = "Saved microchip program to the floppy disk.")
	MutableComponent floppyDiskSave();
	
	@LangKey(text = "Pause")
	MutableComponent guideButtonPause();
	
	@LangKey(text = "Resume")
	MutableComponent guideButtonResume();
	
	@LangKey(text = "Input A")
	MutableComponent guideTooltipInputA();
	
	@LangKey(text = "Input B")
	MutableComponent guideTooltipInputB();
	
	@LangKey(text = "Output")
	MutableComponent guideTooltipOutput();
	
	@LangKey(text = "Sensor")
	@WithStyle("input")
	MutableComponent sensor();
	
	@LangKey(text = "Emitter")
	@WithStyle("output")
	MutableComponent emitter();
	
	@LangKey(text = "Input")
	@WithStyle("input")
	MutableComponent input();
	
	@LangKey(text = "Stores all of your redstone bits and logic components!")
	@WithStyle("tooltip")
	MutableComponent logicArrayHelp1();
	
	@LangKey(text = "Can be opened while in the Microchip menu.")
	@WithStyle("tooltip")
	MutableComponent logicArrayHelp2();
	
	@LangKey(text = "Use %s while holding to open.")
	@WithStyle("tooltip")
	MutableComponent logicArrayHelp3(@Parsed("keybind") @WithStyle("highlighted") String keybind);
	
	@LangKey(text = "Can insert and extract items while in your inventory using %s.")
	@WithStyle("tooltip")
	MutableComponent logicArrayHelp4(@Parsed("keybind") @WithStyle("highlighted") String keybind);
	
	@LangKey(text = "\u2264")
	MutableComponent logicComparisonModeLessThanOrEqualTo();
	
	@LangKey(text = "=")
	MutableComponent logicComparisonModeEqualTo();
	
	@LangKey(text = "\u2265")
	MutableComponent logicComparisonModeGreaterThanOrEqualTo();
	
	@LangKey(text = "Cancel")
	MutableComponent logicConfigButtonLabelCancel();
	
	@LangKey(text = "Chance: ")
	MutableComponent logicConfigButtonLabelChance();
	
	@LangKey(text = "Direction")
	MutableComponent logicConfigButtonLabelDirection();
	
	@LangKey(text = "Duration: ")
	MutableComponent logicConfigButtonLabelDuration();
	
	@LangKey(text = "Inputs: ")
	MutableComponent logicConfigButtonLabelInputs();
	
	@LangKey(text = "Signal Strength: ")
	MutableComponent logicConfigButtonLabelIoSignalStrength();
	
	@LangKey(text = "Mode")
	MutableComponent logicConfigButtonLabelMode();
	
	@LangKey(text = "Outputs: ")
	MutableComponent logicConfigButtonLabelOutputs();
	
	@LangKey(text = "Fill Threshold: ")
	MutableComponent logicConfigButtonLabelReaderFillThreshold();
	
	@LangKey(text = "Signal Threshold: ")
	MutableComponent logicConfigButtonLabelReaderSignalThreshold();
	
	@LangKey(text = "Save")
	MutableComponent logicConfigButtonLabelSave();
	
	@LangKey(text = "Auto Reset")
	MutableComponent logicConfigButtonLabelSequencerAutoReset();
	
	@LangKey(text = "Delay: ")
	MutableComponent logicConfigButtonLabelSequencerDelay();
	
	@LangKey(text = "Reset Port")
	MutableComponent logicConfigButtonLabelSequencerResetPort();
	
	@LangKey(text = "%s ticks (%ss)")
	MutableComponent logicConfigButtonLabelTicksAndSeconds(long ticks, float seconds);
	
	@LangKey(text = "%s tick (%ss)")
	MutableComponent logicConfigButtonLabelTicksAndSecondsSingular(long ticks, float seconds);
	
	@LangKey(text = "Label: ")
	MutableComponent logicConfigButtonLabelTagLabel();
	
	@LangKey(text = "Threshold: ")
	MutableComponent logicConfigButtonLabelTagThreshold();
	
	@LangKey(text = "Global")
	MutableComponent logicConfigButtonLabelTagGlobal();
	
	@LangKey(text = "The time for the output to be on.")
	MutableComponent logicConfigButtonTooltipDuration();
	
	@LangKey(text = "The number of inputs that this component can accept.")
	MutableComponent logicConfigButtonTooltipInputs();
	
	@LangKey(text = "The direction this port should interact with redstone power on.")
	MutableComponent logicConfigButtonTooltipIoDirection();
	
	@LangKey(text = "Whether this port should input or output redstone power.")
	MutableComponent logicConfigButtonTooltipIoMode();
	
	@LangKey(text = "Whether any or all input(s) need to match the signal strength comparison for the output to be ON.")
	MutableComponent logicConfigButtonTooltipComparatorMode();
	
	@LangKey(text = "The input signal must be equal to %s.")
	MutableComponent logicConfigButtonTooltipReaderSignalComparisonModeEqualTo(int signal);
	
	@LangKey(text = "The input signal must be greater than or equal to %s.")
	MutableComponent logicConfigButtonTooltipReaderSignalComparisonModeGreaterThanOrEqualTo(int signal);
	
	@LangKey(text = "The input signal must be less than or equal to %s.")
	MutableComponent logicConfigButtonTooltipReaderSignalComparisonModeLessThanOrEqualTo(int signal);
	
	@LangKey(text = "The input signal must be equal to %s.")
	MutableComponent logicConfigButtonTooltipIoSignalComparisonModeEqualTo(int signal);
	
	@LangKey(text = "The input signal must be greater than or equal to %s.")
	MutableComponent logicConfigButtonTooltipIoSignalComparisonModeGreaterThanOrEqualTo(int signal);
	
	@LangKey(text = "The input signal must be less than or equal to %s.")
	MutableComponent logicConfigButtonTooltipIoSignalComparisonModeLessThanOrEqualTo(int signal);
	
	@LangKey(text = "The output signal will be equal to the signal strength of the wire powering the port.")
	MutableComponent logicConfigButtonTooltipIoSignalComparisonOutputPass();
	
	@LangKey(text = "The output signal will be equal to %s.")
	MutableComponent logicConfigButtonTooltipIoSignalComparisonOutput(int signal);
	
	@LangKey(text = "The redstone signal strength required for the output to be on.")
	MutableComponent logicConfigButtonTooltipIoSignalStrengthInput();
	
	@LangKey(text = "The redstone signal strength that will be outputted.")
	MutableComponent logicConfigButtonTooltipIoSignalStrengthOutput();
	
	@LangKey(text = "At least one input signal must be equal to %s.")
	MutableComponent logicConfigButtonTooltipComparatorAnySignalComparisonModeEqualTo(int signal);
	
	@LangKey(text = "At least one input signal must be greater than or equal to %s.")
	MutableComponent logicConfigButtonTooltipComparatorAnySignalComparisonModeGreaterThanOrEqualTo(int signal);
	
	@LangKey(text = "At least one input signal must be less than or equal to %s.")
	MutableComponent logicConfigButtonTooltipComparatorAnySignalComparisonModeLessThanOrEqualTo(int signal);
	
	@LangKey(text = "At least one input signal must be equal to the first input's signal.")
	MutableComponent logicConfigButtonTooltipComparatorAnyPassSignalComparisonModeEqualTo();
	
	@LangKey(text = "At least one input signal must be greater than or equal to the first input's signal.")
	MutableComponent logicConfigButtonTooltipComparatorAnyPassSignalComparisonModeGreaterThanOrEqualTo();
	
	@LangKey(text = "At least one input signal must be less than or equal to the first input's signal.")
	MutableComponent logicConfigButtonTooltipComparatorAnyPassSignalComparisonModeLessThanOrEqualTo();
	
	@LangKey(text = "All input signals must be equal to %s.")
	MutableComponent logicConfigButtonTooltipComparatorAllSignalComparisonModeEqualTo(int signal);
	
	@LangKey(text = "All input signals must be greater than or equal to %s.")
	MutableComponent logicConfigButtonTooltipComparatorAllSignalComparisonModeGreaterThanOrEqualTo(int signal);
	
	@LangKey(text = "All input signals must be less than or equal to %s.")
	MutableComponent logicConfigButtonTooltipComparatorAllSignalComparisonModeLessThanOrEqualTo(int signal);
	
	@LangKey(text = "All input signals must be equal to the first input's signal.")
	MutableComponent logicConfigButtonTooltipComparatorAllPassSignalComparisonModeEqualTo();
	
	@LangKey(text = "All input signals must be greater than or equal to the first input's signal.")
	MutableComponent logicConfigButtonTooltipComparatorAllPassSignalComparisonModeGreaterThanOrEqualTo();
	
	@LangKey(text = "All input signals must be less than or equal to the first input's signal.")
	MutableComponent logicConfigButtonTooltipComparatorAllPassSignalComparisonModeLessThanOrEqualTo();
	
	@LangKey(text = "The number of outputs that this component can yield.")
	MutableComponent logicConfigButtonTooltipOutputs();
	
	@LangKey(text = "The percentage chance of one of the outputs to be ON for a given tick while the input is ON.")
	MutableComponent logicConfigButtonTooltipRandomizerChance();
	
	@LangKey(text = "The direction this reader should read block capacity from.")
	MutableComponent logicConfigButtonTooltipReaderDirection();
	
	@LangKey(text = "The percentage filled the container capacity must be for the output to be on.")
	MutableComponent logicConfigButtonTooltipReaderFillThreshold();
	
	@LangKey(text = "The type of information to read from the adjacent block.")
	MutableComponent logicConfigButtonTooltipReaderMode();
	
	@LangKey(text = "The input signal (as per a comparator) required for the output to be on.")
	MutableComponent logicConfigButtonTooltipReaderSignalThreshold();
	
	@LangKey(text = "The fill percentage must be equal to %s.")
	MutableComponent logicConfigButtonTooltipReaderThresholdComparisonModeEqualTo(@Parsed("percentage") float threshold);
	
	@LangKey(text = "The fill percentage must be greater than or equal to %s.")
	MutableComponent logicConfigButtonTooltipReaderThresholdComparisonModeGreaterThanOrEqualTo(@Parsed("percentage") float threshold);
	
	@LangKey(text = "The fill percentage must be less than or equal to %s.")
	MutableComponent logicConfigButtonTooltipReaderThresholdComparisonModeLessThanOrEqualTo(@Parsed("percentage") float threshold);
	
	@LangKey(text = "When the first input is ON, the selected output will move up. When the second input is ON, the selected output will move down.")
	MutableComponent logicConfigButtonTooltipSelectorModeCounter();
	
	@LangKey(text = "The lowest output with a corresponding ON input will have an ON output.")
	MutableComponent logicConfigButtonTooltipSelectorModeSetter();
	
	@LangKey(text = "Whether the sequencer should automatically reset its progress immediately after yielding an output of ON.")
	MutableComponent logicConfigButtonTooltipSequencerAutoReset();
	
	@LangKey(text = "The time before the output is on.")
	MutableComponent logicConfigButtonTooltipSequencerDelay();
	
	@LangKey(text = "While input is ON, the sequencer will increment until it has met X ticks and then emit an output of ON.")
	MutableComponent logicConfigButtonTooltipSequencerModeCounter();
	
	@LangKey(text = "While input is ON, the sequencer will increment until it has met X ticks and then emit an output of ON. While input is OFF, the sequencer will decrement.")
	MutableComponent logicConfigButtonTooltipSequencerModeStrong();
	
	@LangKey(text = "After an ON input signal, the sequencer will wait X ticks and then emit an output of ON.")
	MutableComponent logicConfigButtonTooltipSequencerModeWeak();
	
	@LangKey(text = "Whether a second wire port should be added that will forcefully reset the sequencer's progress.")
	MutableComponent logicConfigButtonTooltipSequencerResetPort();
	
	@LangKey(text = "Whether this tag should sense or emit a signal.")
	MutableComponent logicConfigButtonTooltipTagMode();
	
	@LangKey(text = "The label for this tag to emit or sense.")
	MutableComponent logicConfigButtonTooltipTagLabel();
	
	@LangKey(text = "The amount of tag emitters that must be sensed by this sensor to yield an output of ON.")
	MutableComponent logicConfigButtonTooltipTagThreshold();
	
	@LangKey(text = "Whether this tag sensor should detect emitters in microchips placed by anybody, or just yourself.")
	MutableComponent logicConfigButtonTooltipTagGlobal();
	
	@LangKey(text = "Counter")
	MutableComponent logicConfigSelectorModeCounter();
	
	@LangKey(text = "Setter")
	MutableComponent logicConfigSelectorModeSetter();
	
	@LangKey(text = "Counter")
	MutableComponent logicConfigSequencerModeCounter();
	
	@LangKey(text = "Strong")
	MutableComponent logicConfigSequencerModeStrong();
	
	@LangKey(text = "Weak")
	MutableComponent logicConfigSequencerModeWeak();
	
	@LangKey(text = "Configuration:")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltip();
	
	@LangKey(text = "  Chance: %s")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipChance(@Parsed("percentage") @WithStyle("highlighted") float chance);
	
	@LangKey(text = "Use Right Button to edit the config.")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipClickToOpen();
	
	@LangKey(text = "  Direction: %s")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipDirection(Direction direction);
	
	@LangKey(text = "  Duration: %s")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipDuration(@WithStyle("highlighted") Component duration);
	
	@LangKey(datagen = false)
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipDuration(@Parsed("ticks_and_seconds") @WithStyle("highlighted") long duration);
	
	@LangKey(text = "  Inputs: %s")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipInputs(@WithStyle("highlighted") int inputs);
	
	@LangKey(text = "  Signal: %s")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipSignal(@WithStyle("highlighted") Component signal);
	
	@LangKey(datagen = false)
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipSignal(@WithStyle("highlighted") int signal);
	
	@LangKey(text = "  Signal: %s %s")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipIoSignalComparison(
			@WithStyle("highlighted") LogicComparisonMode comparison,
			@WithStyle("highlighted") int signal
	);
	
	@LangKey(text = "  Mode: %s")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipMode(@WithStyle("highlighted") LogicMode mode);
	
	@LangKey(text = "  Outputs: %s")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipOutputs(@WithStyle("highlighted") int outputs);
	
	@LangKey(text = "  Fill: %s %s")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipReaderFillComparison(
			@WithStyle("highlighted") LogicComparisonMode comparison,
			@WithStyle("highlighted") float threshold
	);
	
	@LangKey(text = "  Signal: %s %s")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipReaderSignalComparison(
			@WithStyle("highlighted") LogicComparisonMode comparison,
			@WithStyle("highlighted") int signal
	);
	
	@LangKey(text = "  Auto Reset: %s")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipSequencerAutoReset(@Parsed("yes_no") boolean autoReset);
	
	@LangKey(text = "  Delay: %s")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipSequencerDelay(@Parsed("ticks_and_seconds") @WithStyle("highlighted") long ticks);
	
	@LangKey(text = "  Reset Port: %s")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipSequencerResetPort(@Parsed("yes_no") boolean resetPort);
	
	@LangKey(text = "  Label: %s")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipLabel(@WithStyle("highlighted") String label);
	
	@LangKey(text = "  Threshold: %s")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipThreshold(@WithStyle("highlighted") int threshold);
	
	@LangKey(text = "  Global: %s")
	@WithStyle("tooltip")
	MutableComponent logicConfigTooltipGlobal(@Parsed("yes_no") boolean global);
	
	@LangKey(text = "Q = %s")
	@WithStyle("tooltip")
	MutableComponent logicGateAlgebra(Component algebra);
	
	@LangKey(text = "A \u2227 B")
	MutableComponent logicGateAlgebraAND();
	
	@LangKey(text = "A \u2191 B")
	MutableComponent logicGateAlgebraNAND();
	
	@LangKey(text = "A \u2193 B")
	MutableComponent logicGateAlgebraNOR();
	
	@LangKey(text = "\u00ACA")
	MutableComponent logicGateAlgebraNOT();
	
	@LangKey(text = "A \u2228 B")
	MutableComponent logicGateAlgebraOR();
	
	@LangKey(text = "A \u22BB B")
	MutableComponent logicGateAlgebraXOR();
	
	@LangKey(text = "Output is ON when all inputs are ON, otherwise output is OFF.")
	@WithStyle("tooltip")
	MutableComponent logicHelpANDGate();
	
	@LangKey(text = "Can either input or output a redstone signal in the world on a single face. Multiple I/O ports can be used to input and output from different faces.")
	@WithStyle("tooltip")
	MutableComponent logicHelpIOPort1();
	
	@LangKey(text = "A microchip cannot have both an input and output port on the same face.")
	@WithStyle("tooltip")
	MutableComponent logicHelpIOPort2();
	
	@LangKey(text = "Output is OFF when all inputs are ON, otherwise output is ON.")
	@WithStyle("tooltip")
	MutableComponent logicHelpNANDGate();
	
	@LangKey(text = "Output is ON when all inputs are OFF, otherwise output is OFF.")
	@WithStyle("tooltip")
	MutableComponent logicHelpNORGate();
	
	@LangKey(text = "Output is ON when the input is OFF, and output is OFF when the input is ON.")
	@WithStyle("tooltip")
	MutableComponent logicHelpNOTGate();
	
	@LangKey(text = "Output is ON when any input is ON, otherwise output is OFF.")
	@WithStyle("tooltip")
	MutableComponent logicHelpORGate();
	
	@LangKey(text = "Throttles an input signal and yields an output with a configurable duration. When set to indefinite, the output will be ON so long as the input is ON.")
	@WithStyle("tooltip")
	MutableComponent logicHelpPulseThrottler1();
	
	@LangKey(text = "The output signal can be set to pass through, or to a specific value.")
	@WithStyle("tooltip")
	MutableComponent logicHelpPulseThrottler2();
	
	@LangKey(text = "When the input is ON, a random output will be ON a configurable percentage of the time.")
	@WithStyle("tooltip")
	MutableComponent logicHelpRandomizer();
	
	@LangKey(text = "Checks the filled percentage of an adjacent block and yields an ON signal if the block's filled percentage is greater than or equal to the set fill threshold.")
	@WithStyle("tooltip")
	MutableComponent logicHelpReader1();
	
	@LangKey(text = "Can be used on item, fluid, or energy storages.")
	@WithStyle("tooltip")
	MutableComponent logicHelpReader2();
	
	@LangKey(
			key = "logic_help_rs_nor_latch_1",
			text = "When the reset (R) input is ON, the output is always OFF."
	)
	@WithStyle("tooltip")
	MutableComponent logicHelpRSNORLatch1();
	
	@LangKey(
			key = "logic_help_rs_nor_latch_2",
			text = "When the set (S) input is ON and the reset (R) input is OFF, the output is set to ON and will remain ON until the reset (R) input is ON."
	)
	@WithStyle("tooltip")
	MutableComponent logicHelpRSNORLatch2();
	
	@LangKey(text = "Emits a single ON output and will switch to adjacent ports in a strategy depending on the mode selected.")
	@WithStyle("tooltip")
	MutableComponent logicHelpSelector();
	
	@LangKey(text = "Delays the output signal by the set delay.")
	@WithStyle("tooltip")
	MutableComponent logicHelpSequencer1();
	
	@LangKey(text = "The mode determines the behavior of the sequencer and can be set to Weak, Strong, or Counter.")
	@WithStyle("tooltip")
	MutableComponent logicHelpSequencer2();
	
	@LangKey(text = "By default the sequencer will not reset after yielding an output state. If auto reset is enabled, the progress will be reset immediately after yielding an output of ON. Additionally, if reset port is enabled, a second input port will be available. When the second input port is given an ON input, the progress of the sequencer will be reset. As long as an ON input is present on this input, the sequencer cannot progress.")
	@WithStyle("tooltip")
	MutableComponent logicHelpSequencer3();
	
	@LangKey(text = "When the input goes from OFF to ON, the output signal swaps states.")
	@WithStyle("tooltip")
	MutableComponent logicHelpTFlipFlop();
	
	@LangKey(text = "Output is ON when the amount of ON inputs is odd, otherwise output is OFF.")
	@WithStyle("tooltip")
	MutableComponent logicHelpXORGate();
	
	@LangKey(text = "The mode determines whether the tag is a sensor or emitter.")
	@WithStyle("tooltip")
	MutableComponent logicHelpTag1();
	
	@LangKey(text = """
			Sensors will yield an output of ON only when at least as many emitters as specified by the threshold that \
			have the same label are also ON.""")
	@WithStyle("tooltip")
	MutableComponent logicHelpTag2();
	
	@LangKey(text = "Tag emitters emit across an entire world, ignoring dimensions.")
	@WithStyle("tooltip")
	MutableComponent logicHelpTag3();
	
	@LangKey(text = "The mode determines whether all inputs or at least one must match the comparison.")
	@WithStyle("tooltip")
	MutableComponent logicHelpComparator1();
	
	@LangKey(text = "When the output is ON, the signal strength will always equal the strength being compared against.")
	@WithStyle("tooltip")
	MutableComponent logicHelpComparator2();
	
	@LangKey(text = "No")
	@WithStyle("no")
	MutableComponent no();
	
	@LangKey(text = "Output")
	@WithStyle("output")
	MutableComponent output();
	
	@LangKey(text = "Sticky Note")
	MutableComponent stickyNote();
	
	@LangKey(text = "Edit")
	MutableComponent stickyNoteEdit();
	
	@LangKey(text = "Yes")
	@WithStyle("yes")
	MutableComponent yes();
	
	@LangKey(text = "Pass")
	MutableComponent pass();
	
	@LangKey(text = "Indefinite")
	MutableComponent indefinite();
	
	@LangKey(text = "Any")
	MutableComponent any();
	
	@LangKey(text = "All")
	MutableComponent all();
}
