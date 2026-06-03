package net.swedz.little_big_redstone.microchip.object.logic.pulse;

import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRTooltips;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;

final class PulseThrottlerConfigMenuProvider extends LogicConfigMenuProvider<PulseThrottlerConfig>
{
	public PulseThrottlerConfigMenuProvider(PulseThrottlerConfig config)
	{
		super(config);
	}
	
	private Component stringifyDuration(double value, String string)
	{
		return value == 0 ?
				LBR.text().indefinite() :
				LBRTooltips.TICKS_AND_SECONDS_SLIDER_PARSER.parse(value, string);
	}
	
	private void createDuration(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addSlider(
				LBR.text().logicConfigButtonLabelDuration(),
				Component.empty(),
				LBR.text().logicConfigButtonTooltipDuration(),
				0,
				0,
				width,
				18,
				0,
				60 * 20,
				config.outputDuration(),
				1,
				0,
				this::stringifyDuration,
				(value) -> config = new PulseThrottlerConfig(Math.round(value), config.signalStrength())
		);
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
				LBR.text().logicConfigButtonTooltipIoSignalStrengthOutput(),
				0,
				22,
				width,
				18,
				0,
				15,
				config.signalStrength(),
				1,
				0,
				this::stringifySignalStrength,
				(value) -> config = new PulseThrottlerConfig(config.outputDuration(), (int) Math.round(value))
		);
	}
	
	@Override
	public void create(LogicConfigMenuBuilder builder, int width, int height)
	{
		this.createDuration(builder, width, height);
		this.createSignalStrength(builder, width, height);
	}
}
