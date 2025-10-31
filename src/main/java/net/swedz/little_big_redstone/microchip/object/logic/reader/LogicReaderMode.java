package net.swedz.little_big_redstone.microchip.object.logic.reader;

import net.minecraft.network.chat.MutableComponent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicMode;

import java.util.function.Supplier;

public enum LogicReaderMode implements LogicMode
{
	ITEM(() -> LBR.text().capabilityItem()),
	FLUID(() -> LBR.text().capabilityFluid()),
	ENERGY(() -> LBR.text().capabilityEnergy());
	
	private final Supplier<MutableComponent> label;
	
	LogicReaderMode(Supplier<MutableComponent> label)
	{
		this.label = label;
	}
	
	@Override
	public MutableComponent label()
	{
		return label.get();
	}
}
