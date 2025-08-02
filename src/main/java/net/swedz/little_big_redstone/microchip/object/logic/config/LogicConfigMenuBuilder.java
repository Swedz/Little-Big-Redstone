package net.swedz.little_big_redstone.microchip.object.logic.config;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.swedz.little_big_redstone.gui.logicconfig.button.iconcycle.CheckboxState;
import net.swedz.little_big_redstone.gui.logicconfig.button.cycle.CycleLogicConfigButton;
import net.swedz.little_big_redstone.gui.logicconfig.button.iconcycle.IconCycleLogicConfigButtonIcon;
import net.swedz.little_big_redstone.gui.logicconfig.button.slider.SliderLogicConfigButton;

import java.util.List;
import java.util.function.Consumer;

public interface LogicConfigMenuBuilder
{
	<T> LogicConfigButtonReference addCycleButton(Component name, Component tooltip, int x, int y, int width, int height, boolean displayOnlyValue, T initialValue, List<T> values, CycleLogicConfigButton.ValueStringifier<T> valueStringifier, Consumer<T> onChange);
	
	LogicConfigButtonReference<Double> addSlider(Component prefix, Component suffix, Component tooltip, int x, int y, int width, int height, double minValue, double maxValue, double currentValue, double stepSize, int precision, SliderLogicConfigButton.ValueStringifier valueStringifier, Consumer<Double> onChange);
	
	default LogicConfigButtonReference<Double> addSlider(Component prefix, Component suffix, Component tooltip, int x, int y, int width, int height, double minValue, double maxValue, double currentValue, double stepSize, int precision, Consumer<Double> onChange)
	{
		return this.addSlider(prefix, suffix, tooltip, x, y, width, height, minValue, maxValue, currentValue, stepSize, precision, (value, string) -> Component.literal(string), onChange);
	}
	
	LogicConfigButtonReference<CheckboxState> addCheckbox(Component text, Component tooltip, int x, int y, boolean initialValue, Consumer<Boolean> onChange);
	
	<T extends IconCycleLogicConfigButtonIcon> LogicConfigButtonReference<T> addCycleButton(Component tooltip, int x, int y, ResourceLocation atlas, T initialValue, List<T> values, Consumer<T> onChange);
}
