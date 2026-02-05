package net.swedz.little_big_redstone.microchip.object.logic.config;

import net.minecraft.network.chat.MutableComponent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.gui.logicconfig.widget.iconcycle.IconCycleLogicConfigButtonIcon;

import java.util.function.Supplier;

public enum LogicComparisonMode implements IconCycleLogicConfigButtonIcon
{
	LESS_THAN_OR_EQUAL_TO(() -> LBR.text().logicComparisonModeLessThanOrEqualTo(), 36, 0, (input, setting) -> input <= setting),
	EQUAL_TO(() -> LBR.text().logicComparisonModeEqualTo(), 36 + 18, 0, (input, setting) -> input == setting),
	GREATER_THAN_OR_EQUAL_TO(() -> LBR.text().logicComparisonModeGreaterThanOrEqualTo(), 36 + (18 * 2), 0, (input, setting) -> input >= setting);
	
	private final Supplier<MutableComponent> symbol;
	
	private final int u, v;
	
	private final Test test;
	
	LogicComparisonMode(Supplier<MutableComponent> symbol, int u, int v, Test test)
	{
		this.symbol = symbol;
		this.u = u;
		this.v = v;
		this.test = test;
	}
	
	public MutableComponent symbol()
	{
		return symbol.get();
	}
	
	@Override
	public int u()
	{
		return u;
	}
	
	@Override
	public int v()
	{
		return v;
	}
	
	public boolean test(Number input, Number setting)
	{
		return test.test(input.floatValue(), setting.floatValue());
	}
	
	private interface Test
	{
		boolean test(float input, float setting);
	}
}
