package net.swedz.little_big_redstone.microchip.object.logic.battery;

import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;

final class LogicBatteryConfigMenuProvider extends LogicConfigMenuProvider<LogicBatteryConfig>
{
	public LogicBatteryConfigMenuProvider(LogicBatteryConfig config)
	{
		super(config);
	}
	
	private Component stringifySignalStrength(double value, String string)
	{
		return Component.literal(string);
	}
	
	private void createSignalStrength(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addSlider(
				LBR.text().logicConfigButtonLabelIoSignalStrength(),
				Component.empty(),
				LBR.text().logicConfigButtonTooltipIoSignalStrengthOutput(),
				0,
				0,
				width,
				18,
				0,
				15,
				config.signalStrength(),
				1,
				0,
				this::stringifySignalStrength,
				(value) -> config = new LogicBatteryConfig((int) Math.round(value))
		);
	}
	
	@Override
	public void create(LogicConfigMenuBuilder builder, int width, int height)
	{
		this.createSignalStrength(builder, width, height);
	}
}
