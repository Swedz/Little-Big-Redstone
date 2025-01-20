package net.swedz.little_big_redstone;

import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.swedz.tesseract.neoforge.tooltip.TooltipAttachment;

import java.util.List;
import java.util.Optional;

public final class LBRTooltips
{
	public static final Style DEFAULT_STYLE   = Style.EMPTY.withColor(TextColor.fromRgb(0xa9a9a9)).withItalic(false);
	public static final Style HIGHLIGHT_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(0xffde7d)).withItalic(false);
	
	public static final TooltipAttachment LOGIC_GATE_NO_SHIFT = TooltipAttachment.multilinesOptional(
			(stack, item) -> stack.has(LBRComponents.LOGIC),
			(stack, item) ->
			{
				var logicComponent = stack.get(LBRComponents.LOGIC);
				List<Component> lines = Lists.newArrayList();
				logicComponent.appendNoShiftHoverText(lines);
				return lines.isEmpty() ? Optional.empty() : Optional.of(lines);
			}
	).noShiftRequired();
	
	public static final TooltipAttachment LOGIC_GATE_SHIFT = TooltipAttachment.multilinesOptional(
			(stack, item) -> stack.has(LBRComponents.LOGIC),
			(stack, item) ->
			{
				var logicComponent = stack.get(LBRComponents.LOGIC);
				List<Component> lines = Lists.newArrayList();
				logicComponent.appendShiftHoverText(lines);
				return lines.isEmpty() ? Optional.empty() : Optional.of(lines);
			}
	);
	
	public static void init()
	{
	}
}
