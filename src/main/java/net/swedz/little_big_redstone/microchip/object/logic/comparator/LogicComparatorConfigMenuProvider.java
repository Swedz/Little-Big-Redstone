package net.swedz.little_big_redstone.microchip.object.logic.comparator;

import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicAccumulationMode;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicComparisonMode;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigButtonReference;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;

import java.util.Arrays;

final class LogicComparatorConfigMenuProvider extends LogicConfigMenuProvider<LogicComparatorConfig>
{
	private LogicConfigButtonReference<LogicComparisonMode> comparisonButton;
	
	public LogicComparatorConfigMenuProvider(LogicComparatorConfig config)
	{
		super(config);
	}
	
	private void createMode(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addCycleButton(
				LBR.text().logicConfigButtonLabelMode(),
				LBR.text().logicConfigButtonTooltipComparatorMode(),
				0,
				0,
				width,
				18,
				false,
				config.mode,
				Arrays.asList(LogicAccumulationMode.values()),
				LogicAccumulationMode::label,
				(value) ->
				{
					config.mode = value;
					this.updateComparisonButton();
				}
		);
	}
	
	private Component signalComparisonTooltip()
	{
		if(config.mode == LogicAccumulationMode.ANY)
		{
			if(config.signalStrength == 0)
			{
				return switch(config.signalComparison)
				{
					case LESS_THAN_OR_EQUAL_TO -> LBR.text().logicConfigButtonTooltipComparatorAnyPassSignalComparisonModeLessThanOrEqualTo();
					case EQUAL_TO -> LBR.text().logicConfigButtonTooltipComparatorAnyPassSignalComparisonModeEqualTo();
					case GREATER_THAN_OR_EQUAL_TO -> LBR.text().logicConfigButtonTooltipComparatorAnyPassSignalComparisonModeGreaterThanOrEqualTo();
				};
			}
			else
			{
				return switch(config.signalComparison)
				{
					case LESS_THAN_OR_EQUAL_TO -> LBR.text().logicConfigButtonTooltipComparatorAnySignalComparisonModeLessThanOrEqualTo(config.signalStrength);
					case EQUAL_TO -> LBR.text().logicConfigButtonTooltipComparatorAnySignalComparisonModeEqualTo(config.signalStrength);
					case GREATER_THAN_OR_EQUAL_TO -> LBR.text().logicConfigButtonTooltipComparatorAnySignalComparisonModeGreaterThanOrEqualTo(config.signalStrength);
				};
			}
		}
		else
		{
			if(config.signalStrength == 0)
			{
				return switch(config.signalComparison)
				{
					case LESS_THAN_OR_EQUAL_TO -> LBR.text().logicConfigButtonTooltipComparatorAllPassSignalComparisonModeLessThanOrEqualTo();
					case EQUAL_TO -> LBR.text().logicConfigButtonTooltipComparatorAllPassSignalComparisonModeEqualTo();
					case GREATER_THAN_OR_EQUAL_TO -> LBR.text().logicConfigButtonTooltipComparatorAllPassSignalComparisonModeGreaterThanOrEqualTo();
				};
			}
			else
			{
				return switch(config.signalComparison)
				{
					case LESS_THAN_OR_EQUAL_TO -> LBR.text().logicConfigButtonTooltipComparatorAllSignalComparisonModeLessThanOrEqualTo(config.signalStrength);
					case EQUAL_TO -> LBR.text().logicConfigButtonTooltipComparatorAllSignalComparisonModeEqualTo(config.signalStrength);
					case GREATER_THAN_OR_EQUAL_TO -> LBR.text().logicConfigButtonTooltipComparatorAllSignalComparisonModeGreaterThanOrEqualTo(config.signalStrength);
				};
			}
		}
	}
	
	private void createComparison(LogicConfigMenuBuilder builder, int width, int height)
	{
		comparisonButton = builder.addCycleButton(
				this.signalComparisonTooltip(),
				0,
				22,
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
			comparisonButton.setTooltip(this.signalComparisonTooltip());
		}
	}
	
	private Component stringifySignalStrength(double value, String string)
	{
		return value == 0 ?
				LBR.text().pass() :
				Component.literal(string);
	}
	
	private void createSignalStrength(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addSlider(
				LBR.text().logicConfigButtonLabelIoSignalStrength(),
				Component.empty(),
				LBR.text().logicConfigButtonTooltipIoSignalStrengthInput(),
				18 + 4,
				22,
				width - 18 - 4,
				18,
				0,
				15,
				config.signalStrength,
				1,
				0,
				this::stringifySignalStrength,
				(value) ->
				{
					config.signalStrength = (int) Math.round(value);
					this.updateComparisonButton();
				}
		);
	}
	
	private void createInputs(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addSlider(
				LBR.text().logicConfigButtonLabelInputs(),
				Component.empty(),
				LBR.text().logicConfigButtonTooltipInputs(),
				0,
				22 * 2,
				width,
				18,
				1,
				10,
				config.inputs,
				1,
				0,
				(value) -> config.inputs = (int) Math.round(value)
		);
	}
	
	@Override
	public void create(LogicConfigMenuBuilder builder, int width, int height)
	{
		this.createMode(builder, width, height);
		this.createComparison(builder, width, height);
		this.createSignalStrength(builder, width, height);
		this.createInputs(builder, width, height);
	}
}
