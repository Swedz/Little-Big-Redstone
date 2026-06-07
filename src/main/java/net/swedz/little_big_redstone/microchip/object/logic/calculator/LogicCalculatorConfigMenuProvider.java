package net.swedz.little_big_redstone.microchip.object.logic.calculator;

import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;

import java.util.Arrays;

final class LogicCalculatorConfigMenuProvider extends LogicConfigMenuProvider<LogicCalculatorConfig>
{
	public LogicCalculatorConfigMenuProvider(LogicCalculatorConfig config)
	{
		super(config);
	}
	
	private void createMode(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addCycleButton(
				LBR.text().logicConfigButtonLabelMode(),
				LBR.text().logicConfigButtonTooltipCalculatorMode(),
				0,
				0,
				width,
				18,
				false,
				config.mode(),
				Arrays.asList(LogicCalculatorMode.values()),
				LogicCalculatorMode::label,
				(value) -> config = new LogicCalculatorConfig(value, config.inputPorts())
		);
	}
	
	private void createInputs(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addSlider(
				LBR.text().logicConfigButtonLabelInputs(),
				Component.empty(),
				LBR.text().logicConfigButtonTooltipInputs(),
				0,
				22,
				width,
				18,
				2,
				10,
				config.inputPorts(),
				1,
				0,
				(value) -> config = new LogicCalculatorConfig(config.mode(), (int) Math.round(value))
		);
	}
	
	@Override
	public void create(LogicConfigMenuBuilder builder, int width, int height)
	{
		this.createMode(builder, width, height);
		this.createInputs(builder, width, height);
	}
}
