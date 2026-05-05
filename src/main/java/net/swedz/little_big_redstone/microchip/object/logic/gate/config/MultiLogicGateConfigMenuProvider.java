package net.swedz.little_big_redstone.microchip.object.logic.gate.config;

import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;

final class MultiLogicGateConfigMenuProvider extends LogicConfigMenuProvider<MultiLogicGateConfig>
{
	public MultiLogicGateConfigMenuProvider(MultiLogicGateConfig config)
	{
		super(config);
	}
	
	@Override
	public void create(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addSlider(
				LBR.text().logicConfigButtonLabelInputs(),
				Component.empty(),
				LBR.text().logicConfigButtonTooltipInputs(),
				0, 0,
				width, 18,
				config.inputsAllowed().min(), config.inputsAllowed().max(),
				config.inputs,
				1, 0,
				(value) -> config.inputs = (int) Math.round(value)
		);
	}
}
