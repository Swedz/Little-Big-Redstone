package net.swedz.little_big_redstone.microchip.object.logic.config;

import net.minecraft.network.chat.MutableComponent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicMode;

import java.util.function.Supplier;

public enum LogicAccumulationMode implements LogicMode
{
	ANY(() -> LBR.text().any()),
	ALL(() -> LBR.text().all());
	
	private final Supplier<MutableComponent> label;
	
	LogicAccumulationMode(Supplier<MutableComponent> label)
	{
		this.label = label;
	}
	
	@Override
	public MutableComponent label()
	{
		return label.get();
	}
}
