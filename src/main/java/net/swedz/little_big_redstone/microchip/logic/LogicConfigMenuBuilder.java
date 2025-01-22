package net.swedz.little_big_redstone.microchip.logic;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Function;

public interface LogicConfigMenuBuilder
{
	<T> void addCycleButton(Component name, int x, int y, int width, int height, boolean displayOnlyValue, T initialValue, Function<T, Component> valueStringifier, Consumer<T> onChange);
	
	void addSlider(Component prefix, Component suffix, int x, int y, int width, int height, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString, Consumer<Double> onChange);
	
	void addCheckbox(Component text, int x, int y, boolean initialValue, Consumer<Boolean> onChange);
}
