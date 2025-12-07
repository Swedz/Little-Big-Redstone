package net.swedz.little_big_redstone.microchip.object.logic.reader;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRTooltips;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicComparisonMode;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigButtonReference;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;

import java.util.Arrays;

final class LogicReaderConfigMenuProvider extends LogicConfigMenuProvider<LogicReaderConfig>
{
	private LogicConfigButtonReference<LogicComparisonMode> comparisonButton;
	private LogicConfigButtonReference<Double>              thresholdFillSlider;
	private LogicConfigButtonReference<Double>              thresholdSignalSlider;
	
	public LogicReaderConfigMenuProvider(LogicReaderConfig config)
	{
		super(config);
	}
	
	private void updateComparisonButton()
	{
		if(comparisonButton != null)
		{
			comparisonButton.setTooltip(this.tooltipComparison());
		}
	}
	
	private void updateThresholdSlider()
	{
		boolean readsSignal = config.mode.readsSignal();
		thresholdFillSlider.setVisible(!readsSignal);
		thresholdSignalSlider.setVisible(readsSignal);
	}
	
	private void createMode(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addCycleButton(
				LBR.text().logicConfigButtonLabelMode(),
				LBR.text().logicConfigButtonTooltipReaderMode(),
				0, 0,
				width, 18,
				false,
				config.mode,
				Arrays.asList(LogicReaderMode.values()),
				LogicReaderMode::label,
				(value) ->
				{
					boolean changed = value.readsSignal() != config.mode.readsSignal();
					config.mode = value;
					if(changed)
					{
						this.updateComparisonButton();
						this.updateThresholdSlider();
					}
				}
		);
	}
	
	private void createDirection(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addCycleButton(
				LBR.text().logicConfigButtonLabelDirection(),
				LBR.text().logicConfigButtonTooltipReaderDirection(),
				0, 22,
				width, 18,
				false,
				config.direction,
				Arrays.asList(Direction.values()),
				LBRTooltips.DIRECTION_PARSER::parse,
				(value) -> config.direction = value
		);
	}
	
	private MutableComponent tooltipComparison()
	{
		return config.mode.readsSignal() ?
				this.tooltipSignalComparison() :
				this.tooltipFillComparison();
	}
	
	private MutableComponent tooltipFillComparison()
	{
		return switch (config.comparison)
		{
			case LESS_THAN_OR_EQUAL_TO -> LBR.text().logicConfigButtonTooltipReaderThresholdComparisonModeLessThanOrEqualTo(config.fillThreshold);
			case EQUAL_TO -> LBR.text().logicConfigButtonTooltipReaderThresholdComparisonModeEqualTo(config.fillThreshold);
			case GREATER_THAN_OR_EQUAL_TO -> LBR.text().logicConfigButtonTooltipReaderThresholdComparisonModeGreaterThanOrEqualTo(config.fillThreshold);
		};
	}
	
	private MutableComponent tooltipSignalComparison()
	{
		return switch (config.comparison)
		{
			case LESS_THAN_OR_EQUAL_TO -> LBR.text().logicConfigButtonTooltipReaderSignalComparisonModeLessThanOrEqualTo(config.signalThreshold);
			case EQUAL_TO -> LBR.text().logicConfigButtonTooltipReaderSignalComparisonModeEqualTo(config.signalThreshold);
			case GREATER_THAN_OR_EQUAL_TO -> LBR.text().logicConfigButtonTooltipReaderSignalComparisonModeGreaterThanOrEqualTo(config.signalThreshold);
		};
	}
	
	private void createComparison(LogicConfigMenuBuilder builder, int width, int height)
	{
		comparisonButton = builder.addCycleButton(
				this.tooltipComparison(),
				0, 22 * 2,
				LBR.id("textures/gui/slot_atlas.png"),
				config.comparison,
				Arrays.asList(LogicComparisonMode.values()),
				(value) ->
				{
					config.comparison = value;
					this.updateComparisonButton();
				}
		);
		
		this.updateComparisonButton();
	}
	
	private void createThreshold(LogicConfigMenuBuilder builder, int width, int height)
	{
		thresholdFillSlider = builder.addSlider(
				LBR.text().logicConfigButtonLabelReaderFillThreshold(),
				Component.literal("%"),
				LBR.text().logicConfigButtonTooltipReaderFillThreshold(),
				18 + 4, 22 * 2,
				width - 18 - 4, 18,
				0, 100,
				config.fillThreshold * 100,
				1, 0,
				(value) ->
				{
					config.fillThreshold = (float) (value / 100f);
					this.updateComparisonButton();
				}
		).setVisible(false);
		
		thresholdSignalSlider = builder.addSlider(
				LBR.text().logicConfigButtonLabelReaderSignalThreshold(),
				Component.empty(),
				LBR.text().logicConfigButtonTooltipReaderSignalThreshold(),
				18 + 4, 22 * 2,
				width - 18 - 4, 18,
				1, 15,
				config.signalThreshold,
				1, 0,
				(value) ->
				{
					config.signalThreshold = value.intValue();
					this.updateComparisonButton();
				}
		).setVisible(false);
		
		this.updateThresholdSlider();
	}
	
	@Override
	public void create(LogicConfigMenuBuilder builder, int width, int height)
	{
		this.createMode(builder, width, height);
		this.createDirection(builder, width, height);
		this.createComparison(builder, width, height);
		this.createThreshold(builder, width, height);
	}
}
