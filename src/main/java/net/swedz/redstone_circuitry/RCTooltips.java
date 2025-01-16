package net.swedz.redstone_circuitry;

import net.swedz.tesseract.neoforge.tooltip.TooltipAttachment;

import static net.swedz.tesseract.neoforge.tooltip.TextLine.*;

public final class RCTooltips
{
	private static final TooltipAttachment LOGIC_GATE = TooltipAttachment.singleLine(
			(stack, item) -> stack.has(RCComponents.LOGIC_GATE.get()),
			(stack, item) ->
			{
				var logicGate = stack.get(RCComponents.LOGIC_GATE.get());
				return line(RCText.LOGIC_GATE).arg(logicGate.type().id());
			}
	);
	
	public static void init()
	{
	}
}
