package net.swedz.little_big_redstone.microchip.logic.config;

import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface LogicConfigMenuBuilder
{
	<T> LogicConfigButtonReference addCycleButton(Component name, Component tooltip, int x, int y, int width, int height, boolean displayOnlyValue, T initialValue, List<T> values, Function<T, Component> valueStringifier, Consumer<T> onChange);
	
	LogicConfigButtonReference addSlider(Component prefix, Component suffix, Component tooltip, int x, int y, int width, int height, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString, Consumer<Double> onChange);
	
	LogicConfigButtonReference addCheckbox(Component text, Component tooltip, int x, int y, boolean initialValue, Consumer<Boolean> onChange);
}
