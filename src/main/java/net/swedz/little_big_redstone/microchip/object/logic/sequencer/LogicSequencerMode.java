package net.swedz.little_big_redstone.microchip.object.logic.sequencer;

import net.minecraft.network.chat.MutableComponent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicMode;

import java.util.function.Supplier;

public enum LogicSequencerMode implements LogicMode
{
	WEAK(() -> LBR.text().logicConfigSequencerModeWeak(), () -> LBR.text().logicConfigButtonTooltipSequencerModeWeak()),
	STRONG(() -> LBR.text().logicConfigSequencerModeStrong(), () -> LBR.text().logicConfigButtonTooltipSequencerModeStrong()),
	COUNTER(() -> LBR.text().logicConfigSequencerModeCounter(), () -> LBR.text().logicConfigButtonTooltipSequencerModeCounter());
	
	private final Supplier<MutableComponent> label;
	private final Supplier<MutableComponent> tooltip;
	
	LogicSequencerMode(Supplier<MutableComponent> label, Supplier<MutableComponent> tooltip)
	{
		this.label = label;
		this.tooltip = tooltip;
	}
	
	public MutableComponent label()
	{
		return label.get();
	}
	
	public MutableComponent tooltip()
	{
		return tooltip.get();
	}
}
