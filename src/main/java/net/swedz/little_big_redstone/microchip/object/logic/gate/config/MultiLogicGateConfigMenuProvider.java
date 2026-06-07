package net.swedz.little_big_redstone.microchip.object.logic.gate.config;

import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;

import java.util.function.Function;

final class MultiLogicGateConfigMenuProvider<C extends MultiLogicGateConfig<C>> extends LogicConfigMenuProvider<C>
{
	private final Function<Integer, C> mutator;
	
	public MultiLogicGateConfigMenuProvider(C config, Function<Integer, C> mutator)
	{
		super(config);
		this.mutator = mutator;
	}
	
	@Override
	public void create(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addSlider(
				LBR.text().logicConfigButtonLabelInputs(),
				Component.empty(),
				LBR.text().logicConfigButtonTooltipInputs(),
				0,
				0,
				width,
				18,
				config.inputPortsAllowed().min(),
				config.inputPortsAllowed().max(),
				config.inputPorts(),
				1,
				0,
				(value) -> config = mutator.apply((int) Math.round(value))
		);
	}
}
