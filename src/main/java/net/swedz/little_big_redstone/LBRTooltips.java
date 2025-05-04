package net.swedz.little_big_redstone;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.swedz.little_big_redstone.microchip.logic.reader.LogicReaderMode;
import net.swedz.little_big_redstone.microchip.logic.selector.LogicSelectorMode;
import net.swedz.little_big_redstone.microchip.logic.sequencer.LogicSequencerMode;
import net.swedz.tesseract.neoforge.tooltip.BiParser;
import net.swedz.tesseract.neoforge.tooltip.Parser;
import net.swedz.tesseract.neoforge.tooltip.TooltipAttachment;

public final class LBRTooltips
{
	public static final Style DEFAULT_STYLE   = Style.EMPTY.withColor(TextColor.fromRgb(0xA9A9A9)).withItalic(false);
	public static final Style HIGHLIGHT_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(0xFFDE7D)).withItalic(false);
	public static final Style YES_STYLE       = Style.EMPTY.withColor(TextColor.fromRgb(0x7FFF7D)).withItalic(false);
	public static final Style NO_STYLE        = Style.EMPTY.withColor(TextColor.fromRgb(0xFF7D7F)).withItalic(false);
	public static final Style INPUT_STYLE     = Style.EMPTY.withColor(TextColor.fromRgb(0x7D9EFF)).withItalic(false);
	public static final Style OUTPUT_STYLE    = Style.EMPTY.withColor(TextColor.fromRgb(0xFF9D7D)).withItalic(false);
	
	public static Style directionStyle(Direction direction)
	{
		return Style.EMPTY.withColor(TextColor.fromRgb(switch (direction)
		{
			case DOWN -> 0xFFD800;
			case UP -> 0xFFFFFF;
			case NORTH -> 0x4CFF00;
			case SOUTH -> 0x0094FF;
			case WEST -> 0xFF6A00;
			case EAST -> 0xFF0000;
		})).withItalic(false);
	}
	
	public static final Parser<Boolean> INPUT_OUTPUT_PARSER = (input) -> input ? LBRText.INPUT.text().withStyle(INPUT_STYLE) : LBRText.OUTPUT.text().withStyle(OUTPUT_STYLE);
	
	public static final Parser<Direction> DIRECTION_PARSER = (direction) -> (switch (direction)
	{
		case DOWN -> LBRText.DIRECTION_DOWN;
		case UP -> LBRText.DIRECTION_UP;
		case NORTH -> LBRText.DIRECTION_NORTH;
		case SOUTH -> LBRText.DIRECTION_SOUTH;
		case WEST -> LBRText.DIRECTION_WEST;
		case EAST -> LBRText.DIRECTION_EAST;
	}).text().withStyle(directionStyle(direction));
	
	public static final Parser<Boolean> BOOLEAN_YES_NO_PARSER = (value) -> value ? LBRText.YES.text().withStyle(YES_STYLE) : LBRText.NO.text().withStyle(NO_STYLE);
	
	public static final Parser<LogicReaderMode> READER_MODE_PARSER = (value) -> (switch (value)
	{
		case ITEM -> LBRText.CAPABILITY_ITEM;
		case FLUID -> LBRText.CAPABILITY_FLUID;
		case ENERGY -> LBRText.CAPABILITY_ENERGY;
	}).text().withStyle(HIGHLIGHT_STYLE);
	
	public static final Parser<LogicSequencerMode> SEQUENCER_MODE_PARSER = (value) -> value.label().text().withStyle(HIGHLIGHT_STYLE);
	
	public static final Parser<LogicSelectorMode> SELECTOR_MODE_PARSER = (value) -> value.label().text().withStyle(HIGHLIGHT_STYLE);
	
	private static LBRText ticksAndSecondsText(Number value)
	{
		return value.intValue() != 1 ? LBRText.LOGIC_CONFIG_BUTTON_LABEL_TICKS_AND_SECONDS : LBRText.LOGIC_CONFIG_BUTTON_LABEL_TICKS_AND_SECONDS_SINGULAR;
	}
	
	public static final Parser<Long>             TICKS_AND_SECONDS_PARSER        = (value) -> ticksAndSecondsText(value).text(value, String.format("%.2f", value / 20f)).withStyle(LBRTooltips.HIGHLIGHT_STYLE);
	public static final BiParser<Double, String> TICKS_AND_SECONDS_SLIDER_PARSER = (value, string) -> ticksAndSecondsText(value).text(string, String.format("%.2f", value / 20));
	
	public static final Parser<Object> DEFAULT_PARSER = (value) ->
	{
		var style = DEFAULT_STYLE;
		if(value instanceof Integer ||
		   value instanceof Long ||
		   value instanceof Double ||
		   value instanceof Float)
		{
			style = HIGHLIGHT_STYLE;
		}
		else if(value instanceof Boolean bool)
		{
			return BOOLEAN_YES_NO_PARSER.parse(bool);
		}
		else if(value instanceof Component component)
		{
			return component.copy().withStyle(style);
		}
		return Component.literal(String.valueOf(value)).withStyle(style);
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
	
	public static void init()
	{
	}
}
