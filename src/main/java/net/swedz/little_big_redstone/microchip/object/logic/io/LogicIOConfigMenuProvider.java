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
	private LogicConfigButtonReference<Double>              signalStrengthSlider;
	
	public LogicIOConfigMenuProvider(LogicIOConfig config)
	{
		super(config);
	}
	
	private void updateComparisonButton()
	{
		if(comparisonButton != null)
		{
			comparisonButton.setTooltip(comparisonButton.isActive() ?
					this.signalComparisonTooltip() :
					LBR.text().logicConfigButtonTooltipIoSignalComparisonOutput(config.signalStrength));
		}
	}
	
	private void createMode(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addCycleButton(
				LBR.text().logicConfigButtonLabelMode(),
				LBR.text().logicConfigButtonTooltipIoMode(),
				0, 0,
				width, 18,
				false,
				config.input,
				List.of(true, false),
				(value) -> LBRTooltips.INPUT_OUTPUT_PARSER.parse(value).plainCopy(),
				(value) ->
				{
					config.input = value;
					config.signalStrength = config.input ? 1 : 15;
					if(signalStrengthSlider != null)
					{
						signalStrengthSlider.setValue((double) config.signalStrength);
						signalStrengthSlider.setTooltip(this.tooltipSignalStrength());
					}
					if(comparisonButton != null)
					{
						comparisonButton.setActive(config.input);
					}
					this.updateComparisonButton();
				}
		);
	}
	
	private void createDirection(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addCycleButton(
				LBR.text().logicConfigButtonLabelDirection(),
				LBR.text().logicConfigButtonTooltipIoDirection(),
				0, 22,
				width, 18,
				false,
				config.direction,
				Arrays.asList(Direction.values()),
				LBRTooltips.DIRECTION_PARSER::parse,
				(value) -> config.direction = value
		);
	}
	
	private Component signalComparisonTooltip()
	{
		return switch (config.signalComparison)
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
				0, 22 * 2,
				LBR.id("textures/gui/slot_atlas.png"),
				config.signalComparison,
				Arrays.asList(LogicComparisonMode.values()),
				(value) ->
				{
					config.signalComparison = value;
					this.updateComparisonButton();
				}
		).setActive(config.input);
		
		this.updateComparisonButton();
	}
	
	private Component tooltipSignalStrength()
	{
		return config.input ?
				LBR.text().logicConfigButtonTooltipIoSignalStrengthInput() :
				LBR.text().logicConfigButtonTooltipIoSignalStrengthOutput();
	}
	
	private void createSignalStrength(LogicConfigMenuBuilder builder, int width, int height)
	{
		signalStrengthSlider = builder.addSlider(
				LBR.text().logicConfigButtonLabelIoSignalStrength(),
				Component.empty(),
				this.tooltipSignalStrength(),
				18 + 4, 22 * 2,
				width - 18 - 4, 18,
				1, 15,
				config.signalStrength,
				1, 0,
				(value) ->
				{
					config.signalStrength = (int) Math.round(value);
					this.updateComparisonButton();
				}
		);
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
