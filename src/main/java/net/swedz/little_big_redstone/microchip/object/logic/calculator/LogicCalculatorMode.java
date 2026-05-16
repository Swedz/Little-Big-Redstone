package net.swedz.little_big_redstone.microchip.object.logic.calculator;

import net.minecraft.network.chat.MutableComponent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicMode;

import java.util.Locale;
import java.util.function.Supplier;

public enum LogicCalculatorMode implements LogicMode
{
	ADDITION(() -> LBR.text().logicConfigCalculatorModeAddition()),
	SUBTRACTION(() -> LBR.text().logicConfigCalculatorModeSubtraction());
	
	private final Supplier<MutableComponent> label;
	
	LogicCalculatorMode(Supplier<MutableComponent> label)
	{
		this.label = label;
	}
	
	@Override
	public MutableComponent label()
	{
		return label.get();
	}
	
	public String textureKey()
	{
		return this.name().toLowerCase(Locale.ROOT);
	}
}
