package net.swedz.little_big_redstone.microchip.object.logic.tag;

import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRTooltips;
import net.swedz.little_big_redstone.gui.logicconfig.widget.iconcycle.CheckboxState;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigButtonReference;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;

import java.util.List;

final class LogicTagConfigMenuProvider extends LogicConfigMenuProvider<LogicTagConfig>
{
	private LogicConfigButtonReference<Double>        thresholdSlider;
	private LogicConfigButtonReference<CheckboxState> globalButton;
	
	public LogicTagConfigMenuProvider(LogicTagConfig config)
	{
		super(config);
	}
	
	private void createMode(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addCycleButton(
				LBR.text().logicConfigButtonLabelMode(),
				LBR.text().logicConfigButtonTooltipTagMode(),
				0,
				0,
				width,
				18,
				false,
				config.input,
				List.of(true, false),
				(value) -> LBRTooltips.SENSOR_EMITTER_PARSER.parse(value).plainCopy(),
				(value) ->
				{
					config.input = value;
					if(thresholdSlider != null)
					{
						thresholdSlider.setVisible(config.input);
					}
					if(globalButton != null)
					{
						globalButton.setVisible(config.input);
					}
				}
		);
	}
	
	private void createLabel(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addTextBox(
				LBR.text().logicConfigButtonLabelTagLabel(),
				LBR.text().logicConfigButtonTooltipTagLabel(),
				0,
				22,
				width,
				18,
				config.label.label(),
				LogicTagLabel.MAX_LENGTH,
				(value) -> value.matches(LogicTagLabel.PATTERN),
				(value) -> config.label = new LogicTagLabel(value)
		);
	}
	
	private void createThreshold(LogicConfigMenuBuilder builder, int width, int height)
	{
		thresholdSlider = builder.addSlider(
				LBR.text().logicConfigButtonLabelTagThreshold(),
				Component.empty(),
				LBR.text().logicConfigButtonTooltipTagThreshold(),
				0,
				22 * 2,
				width,
				18,
				1,
				100,
				config.threshold,
				1,
				0,
				(value) -> config.threshold = value.intValue()
		).setVisible(config.input);
	}
	
	private void createGlobal(LogicConfigMenuBuilder builder, int width, int height)
	{
		globalButton = builder.addCheckbox(
				LBR.text().logicConfigButtonLabelTagGlobal(),
				LBR.text().logicConfigButtonTooltipTagGlobal(),
				0,
				22 * 3,
				config.global,
				(value) -> config.global = value
		).setVisible(config.input);
	}
	
	@Override
	public void create(LogicConfigMenuBuilder builder, int width, int height)
	{
		this.createMode(builder, width, height);
		this.createLabel(builder, width, height);
		this.createThreshold(builder, width, height);
		this.createGlobal(builder, width, height);
	}
}
