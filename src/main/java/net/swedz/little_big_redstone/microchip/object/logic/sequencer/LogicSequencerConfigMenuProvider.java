package net.swedz.little_big_redstone.microchip.object.logic.sequencer;

import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRTooltips;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigButtonReference;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;

import java.util.Arrays;

final class LogicSequencerConfigMenuProvider extends LogicConfigMenuProvider<LogicSequencerConfig>
{
	private LogicConfigButtonReference<LogicSequencerMode> modeButton;
	
	public LogicSequencerConfigMenuProvider(LogicSequencerConfig config)
	{
		super(config);
	}
	
	private void createMode(LogicConfigMenuBuilder builder, int width, int height)
	{
		modeButton = builder.addCycleButton(
				LBR.text().logicConfigButtonLabelMode(),
				config.mode().tooltip(),
				0,
				0,
				width,
				18,
				false,
				config.mode(),
				Arrays.asList(LogicSequencerMode.values()),
				LogicSequencerMode::label,
				(value) ->
				{
					config = new LogicSequencerConfig(value, config.outputDelay(), config.autoReset(), config.resetPort());
					if(modeButton != null)
					{
						modeButton.setTooltip(config.mode().tooltip());
					}
				}
		);
	}
	
	private void createDelay(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addSlider(
				LBR.text().logicConfigButtonLabelSequencerDelay(),
				Component.empty(),
				LBR.text().logicConfigButtonTooltipSequencerDelay(),
				0,
				22,
				width,
				18,
				1,
				60 * 20,
				config.outputDelay(),
				1,
				0,
				LBRTooltips.TICKS_AND_SECONDS_SLIDER_PARSER::parse,
				(value) -> config = new LogicSequencerConfig(config.mode(), Math.round(value), config.autoReset(), config.resetPort())
		);
	}
	
	private void createAutoReset(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addCheckbox(
				LBR.text().logicConfigButtonLabelSequencerAutoReset(),
				LBR.text().logicConfigButtonTooltipSequencerAutoReset(),
				0,
				22 * 2,
				config.autoReset(),
				(value) -> config = new LogicSequencerConfig(config.mode(), config.outputDelay(), value, config.resetPort())
		);
	}
	
	private void createResetPort(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addCheckbox(
				LBR.text().logicConfigButtonLabelSequencerResetPort(),
				LBR.text().logicConfigButtonTooltipSequencerResetPort(),
				0,
				22 * 3,
				config.resetPort(),
				(value) -> config = new LogicSequencerConfig(config.mode(), config.outputDelay(), config.autoReset(), value)
		);
	}
	
	@Override
	public void create(LogicConfigMenuBuilder builder, int width, int height)
	{
		this.createMode(builder, width, height);
		this.createDelay(builder, width, height);
		this.createAutoReset(builder, width, height);
		this.createResetPort(builder, width, height);
	}
}
