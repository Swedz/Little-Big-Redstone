package net.swedz.little_big_redstone.microchip.object.logic.config;

import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface LogicConfigMenuBuilder
{
	<T> LogicConfigButtonReference addCycleButton(Component name, Component tooltip, int x, int y, int width, int height, boolean displayOnlyValue, T initialValue, List<T> values, Function<T, Component> valueStringifier, Consumer<T> onChange);
	
	LogicConfigButtonReference<Double> addSlider(Component prefix, Component suffix, Component tooltip, int x, int y, int width, int height, double minValue, double maxValue, double currentValue, double stepSize, int precision, BiFunction<Double, String, Component> valueStringifier, Consumer<Double> onChange);
	
	default LogicConfigButtonReference<Double> addSlider(Component prefix, Component suffix, Component tooltip, int x, int y, int width, int height, double minValue, double maxValue, double currentValue, double stepSize, int precision, Consumer<Double> onChange)
	{
		return this.addSlider(prefix, suffix, tooltip, x, y, width, height, minValue, maxValue, currentValue, stepSize, precision, (value, string) -> Component.literal(string), onChange);
	}
	
	LogicConfigButtonReference<Boolean> addCheckbox(Component text, Component tooltip, int x, int y, boolean initialValue, Consumer<Boolean> onChange);
}
