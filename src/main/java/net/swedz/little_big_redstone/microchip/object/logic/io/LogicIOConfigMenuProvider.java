package net.swedz.little_big_redstone.microchip.object.logic.io;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRTooltips;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicComparisonMode;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigButtonReference;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;

import java.util.Arrays;
import java.util.List;

final class LogicIOConfigMenuProvider extends LogicConfigMenuProvider<LogicIOConfig>
{
	private LogicConfigButtonReference<LogicComparisonMode> comparisonButton;
	private LogicConfigButtonReference<Double>              inputSignalStrengthSlider;
	private LogicConfigButtonReference<Double>              outputSignalStrengthSlider;
	
	public LogicIOConfigMenuProvider(LogicIOConfig config)
	{
		super(config);
	}
	
	private void createMode(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addCycleButton(
				LBR.text().logicConfigButtonLabelMode(),
				LBR.text().logicConfigButtonTooltipIoMode(),
				0,
				0,
				width,
				18,
				false,
				config.input,
				List.of(true, false),
				(value) -> LBRTooltips.INPUT_OUTPUT_PARSER.parse(value).plainCopy(),
				(value) ->
				{
					config.input = value;
					this.updateSignalStrengthButton();
					this.updateComparisonButton();
				}
		);
	}
	
	private void createDirection(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addCycleButton(
				LBR.text().logicConfigButtonLabelDirection(),
				LBR.text().logicConfigButtonTooltipIoDirection(),
				0,
				22,
				width,
				18,
				false,
				config.direction,
				Arrays.asList(Direction.values()),
				LBRTooltips.DIRECTION_PARSER::parse,
				(value) -> config.direction = value
		);
	}
	
	private Component signalComparisonTooltip()
	{
		return switch(config.signalComparison)
		{
			case LESS_THAN_OR_EQUAL_TO -> LBR.text().logicConfigButtonTooltipIoSignalComparisonModeLessThanOrEqualTo(config.signalStrength);
			case EQUAL_TO -> LBR.text().logicConfigButtonTooltipIoSignalComparisonModeEqualTo(config.signalStrength);
			case GREATER_THAN_OR_EQUAL_TO -> LBR.text().logicConfigButtonTooltipIoSignalComparisonModeGreaterThanOrEqualTo(config.signalStrength);
		};
	}
	
	private void createComparison(LogicConfigMenuBuilder builder, int width, int height)
	{
		comparisonButton = builder.addCycleButton(
				this.signalComparisonTooltip(),
				0,
				22 * 2,
				LBR.id("textures/gui/slot_atlas.png"),
				config.signalComparison,
				Arrays.asList(LogicComparisonMode.values()),
				(value) ->
				{
					config.signalComparison = value;
					this.updateComparisonButton();
				}
		);
		
		this.updateComparisonButton();
	}
	
	private void updateComparisonButton()
	{
		if(comparisonButton != null)
		{
			comparisonButton.setTooltip(comparisonButton.isActive() ?
					this.signalComparisonTooltip() :
					(config.signalStrength == 0 ?
							LBR.text().logicConfigButtonTooltipIoSignalComparisonOutputPass() :
							LBR.text().logicConfigButtonTooltipIoSignalComparisonOutput(config.signalStrength)));
			
			comparisonButton.setActive(config.input);
		}
	}
	
	private Component tooltipSignalStrength()
	{
		return config.input ?
				LBR.text().logicConfigButtonTooltipIoSignalStrengthInput() :
				LBR.text().logicConfigButtonTooltipIoSignalStrengthOutput();
	}
	
	private Component stringifySignalStrength(boolean input, double value, String string)
	{
		return !input && value == 0 ?
				LBR.text().pass() :
				Component.literal(string);
	}
	
	private LogicConfigButtonReference<Double> createSignalStrengthButton(LogicConfigMenuBuilder builder, int width, int height, boolean input)
	{
		return builder.addSlider(
				LBR.text().logicConfigButtonLabelIoSignalStrength(),
				Component.empty(),
				this.tooltipSignalStrength(),
				18 + 4,
				22 * 2,
				width - 18 - 4,
				18,
				input ? 1 : 0,
				15,
				config.signalStrength,
				1,
				0,
				(value, string) -> this.stringifySignalStrength(input, value, string),
				(value) ->
				{
					config.signalStrength = (int) Math.round(value);
					this.updateComparisonButton();
				}
		);
	}
	
	private void createSignalStrength(LogicConfigMenuBuilder builder, int width, int height)
	{
		inputSignalStrengthSlider = this.createSignalStrengthButton(builder, width, height, true);
		outputSignalStrengthSlider = this.createSignalStrengthButton(builder, width, height, false);
		
		this.updateSignalStrengthButton();
	}
	
	private void updateSignalStrengthButton()
	{
		if(inputSignalStrengthSlider != null)
		{
			inputSignalStrengthSlider.setVisible(config.input);
		}
		if(outputSignalStrengthSlider != null)
		{
			outputSignalStrengthSlider.setVisible(!config.input);
		}
	}
	
	@Override
	public void create(LogicConfigMenuBuilder builder, int width, int height)
	{
		this.createMode(builder, width, height);
		this.createDirection(builder, width, height);
		this.createComparison(builder, width, height);
		this.createSignalStrength(builder, width, height);
	}
}
