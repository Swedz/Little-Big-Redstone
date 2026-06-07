package net.swedz.little_big_redstone.microchip.object.logic.randomizer;

import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;

final class LogicRandomizerConfigMenuProvider extends LogicConfigMenuProvider<LogicRandomizerConfig>
{
	public LogicRandomizerConfigMenuProvider(LogicRandomizerConfig config)
	{
		super(config);
	}
	
	private void createOutputs(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addSlider(
				LBR.text().logicConfigButtonLabelOutputs(),
				Component.empty(),
				LBR.text().logicConfigButtonTooltipOutputs(),
				0,
				0,
				width,
				18,
				config.outputPortsAllowed().min(),
				config.outputPortsAllowed().max(),
				config.outputPorts(),
				1,
				0,
				(value) -> config = new LogicRandomizerConfig((int) Math.round(value), config.chance())
		);
	}
	
	private void createChance(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addSlider(
				LBR.text().logicConfigButtonLabelChance(),
				Component.literal("%"),
				LBR.text().logicConfigButtonTooltipRandomizerChance(),
				0,
				22,
				width,
				18,
				1,
				100,
				config.chance() * 100,
				1,
				0,
				(value) -> config = new LogicRandomizerConfig(config.outputPorts(), (float) (value / 100f))
		);
	}
	
	@Override
	public void create(LogicConfigMenuBuilder builder, int width, int height)
	{
		this.createOutputs(builder, width, height);
		this.createChance(builder, width, height);
	}
}
