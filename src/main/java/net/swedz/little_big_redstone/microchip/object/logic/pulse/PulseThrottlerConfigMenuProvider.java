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
	
	private void createDuration(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addSlider(
				LBR.text().logicConfigButtonLabelDuration(),
				Component.empty(),
				LBR.text().logicConfigButtonTooltipDuration(),
				0, 0,
				width, 18,
				1, 60 * 20,
				config.outputDuration,
				1, 0,
				LBRTooltips.TICKS_AND_SECONDS_SLIDER_PARSER::parse,
				(value) -> config.outputDuration = value.intValue()
		);
	}
	
	@Override
	public void create(LogicConfigMenuBuilder builder, int width, int height)
	{
		this.createDuration(builder, width, height);
	}
}
