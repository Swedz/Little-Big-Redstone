package net.swedz.redstone_circuitry;

import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.swedz.tesseract.neoforge.tooltip.TooltipAttachment;

import java.util.List;
import java.util.Optional;

public final class RCTooltips
{
	public static final Style DEFAULT_STYLE   = Style.EMPTY.withColor(TextColor.fromRgb(0xa9a9a9)).withItalic(false);
	public static final Style HIGHLIGHT_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(0xffde7d)).withItalic(false);
	
	public static final TooltipAttachment LOGIC_GATE_NO_SHIFT = TooltipAttachment.multilinesOptional(
			(stack, item) -> stack.has(RCComponents.LOGIC_GATE.get()),
			(stack, item) ->
			{
				var logicGate = stack.get(RCComponents.LOGIC_GATE.get());
				List<Component> lines = Lists.newArrayList();
				logicGate.appendNoShiftHoverText(lines);
				return lines.isEmpty() ? Optional.empty() : Optional.of(lines);
			}
	).noShiftRequired();
	
	public static final TooltipAttachment LOGIC_GATE_SHIFT = TooltipAttachment.multilinesOptional(
			(stack, item) -> stack.has(RCComponents.LOGIC_GATE.get()),
			(stack, item) ->
			{
				var logicGate = stack.get(RCComponents.LOGIC_GATE.get());
				List<Component> lines = Lists.newArrayList();
				logicGate.appendShiftHoverText(lines);
				return lines.isEmpty() ? Optional.empty() : Optional.of(lines);
			}
	);
	
	public static void init()
	{
	}
}
