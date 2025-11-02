package net.swedz.little_big_redstone.microchip.object.logic.selector;

import net.minecraft.network.chat.MutableComponent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicMode;

import java.util.function.Supplier;

public enum LogicSelectorMode implements LogicMode
{
	COUNTER(() -> LBR.text().logicConfigSelectorModeCounter(), () -> LBR.text().logicConfigButtonTooltipSelectorModeCounter()),
	SETTER(() -> LBR.text().logicConfigSelectorModeSetter(), () -> LBR.text().logicConfigButtonTooltipSelectorModeSetter());
	
	private final Supplier<MutableComponent> label;
	private final Supplier<MutableComponent> tooltip;
	
	LogicSelectorMode(Supplier<MutableComponent> label, Supplier<MutableComponent> tooltip)
	{
		this.label = label;
		this.tooltip = tooltip;
	}
	
	@Override
	public MutableComponent label()
	{
		return label.get();
	}
	
	public MutableComponent tooltip()
	{
		return tooltip.get();
	}
}
