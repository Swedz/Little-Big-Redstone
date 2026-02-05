package net.swedz.little_big_redstone;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.swedz.tesseract.neoforge.tooltip.BiParser;
import net.swedz.tesseract.neoforge.tooltip.Parser;
import net.swedz.tesseract.neoforge.tooltip.TooltipAttachment;

import java.util.List;

public final class LBRTooltips
{
	public static final Parser<Boolean> INPUT_OUTPUT_PARSER = (input) -> input ? LBR.text().input() : LBR.text().output();
	public static final Parser<Boolean> SENSOR_EMITTER_PARSER = (input) -> input ? LBR.text().sensor() : LBR.text().emitter();
	
	public static final Parser<Direction> DIRECTION_PARSER = (direction) -> (switch (direction)
	{
		case DOWN -> LBR.text().directionDown();
		case UP -> LBR.text().directionUp();
		case NORTH -> LBR.text().directionNorth();
		case SOUTH -> LBR.text().directionSouth();
		case WEST -> LBR.text().directionWest();
		case EAST -> LBR.text().directionEast();
	});
	
	public static final Parser<Boolean> BOOLEAN_YES_NO_PARSER = (value) -> value ? LBR.text().yes() : LBR.text().no();
	
	public static final BiParser<Double, String> TICKS_AND_SECONDS_SLIDER_PARSER = (value, string) ->
	{
		boolean singular = value.intValue() == 1;
		long ticks = Long.parseLong(string);
		float seconds = value.floatValue() / 20f;
		return singular ?
				LBR.text().logicConfigButtonLabelTicksAndSecondsSingular(ticks, seconds) :
				LBR.text().logicConfigButtonLabelTicksAndSeconds(ticks, seconds);
	};
	
	public static final TooltipAttachment LOGIC_GATE_NO_SHIFT = TooltipAttachment.multilinesOptional(
			(stack, item) -> stack.has(LBRComponents.LOGIC),
			(flag, context, stack, item) ->
			{
				var logicComponent = stack.get(LBRComponents.LOGIC);
				return logicComponent.type().tooltip(logicComponent, false, false);
			}
	).noShiftRequired();
	
	public static final TooltipAttachment LOGIC_GATE_SHIFT = TooltipAttachment.multilinesOptional(
			(stack, item) -> stack.has(LBRComponents.LOGIC),
			(flag, context, stack, item) ->
			{
				var logicComponent = stack.get(LBRComponents.LOGIC);
				return logicComponent.type().tooltip(logicComponent, true, true);
			}
	);
	
	public static final TooltipAttachment LOGIC_ARRAY = TooltipAttachment.multilines(
			(stack, item) -> stack.is(LBRTags.Items.LOGIC_ARRAYS),
			List.of(
					LBR.text().logicArrayHelp1(),
					LBR.text().logicArrayHelp2(),
					Component.empty(),
					LBR.text().logicArrayHelp3("use"),
					LBR.text().logicArrayHelp4("use")
			)
	);
	
	public static final TooltipAttachment FLOPPY_DISK = TooltipAttachment.multilines(
			(stack, item) -> stack.is(LBRTags.Items.FLOPPY_DISKS),
			List.of(
					LBR.text().floppyDiskHelp1(),
					Component.empty(),
					LBR.text().floppyDiskHelp2("sneak", "use"),
					LBR.text().floppyDiskHelp3("use"),
					LBR.text().floppyDiskHelp4("use")
			)
	);
	
	public static void init()
	{
	}
}
