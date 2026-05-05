package net.swedz.little_big_redstone.microchip.object.logic.selector;

import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigButtonReference;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;

import java.util.Arrays;

final class LogicSelectorConfigMenuProvider extends LogicConfigMenuProvider<LogicSelectorConfig>
{
	private LogicConfigButtonReference<LogicSelectorMode> modeButton;
	
	public LogicSelectorConfigMenuProvider(LogicSelectorConfig config)
	{
		super(config);
	}
	
	private void createMode(LogicConfigMenuBuilder builder, int width, int height)
	{
		modeButton = builder.addCycleButton(
				LBR.text().logicConfigButtonLabelMode(),
				config.mode.tooltip(),
				0, 0,
				width, 18,
				false,
				config.mode,
				Arrays.asList(LogicSelectorMode.values()),
				LogicSelectorMode::label,
				(value) ->
				{
					config.mode = value;
					if(modeButton != null)
					{
						modeButton.setTooltip(config.mode.tooltip());
					}
				}
		);
	}
	
	private void createOutputs(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addSlider(
				LBR.text().logicConfigButtonLabelOutputs(),
				Component.empty(),
				LBR.text().logicConfigButtonTooltipOutputs(),
				0, 22,
				width, 18,
				config.outputsAllowed().min(), config.outputsAllowed().max(),
				config.outputs,
				1, 0,
				(value) -> config.outputs = (int) Math.round(value)
		);
	}
	
	@Override
	public void create(LogicConfigMenuBuilder builder, int width, int height)
	{
		this.createMode(builder, width, height);
		this.createOutputs(builder, width, height);
	}
}
