package net.swedz.little_big_redstone.microchip.object.logic.reader;

import net.minecraft.network.chat.MutableComponent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicMode;

import java.util.function.Supplier;

public enum LogicReaderMode implements LogicMode
{
	ITEM(false, () -> LBR.text().capabilityItem()),
	FLUID(false, () -> LBR.text().capabilityFluid()),
	ENERGY(false, () -> LBR.text().capabilityEnergy()),
	COMPARATOR(true, () -> LBR.text().capabilityComparator());
	
	private final boolean                    readsSignal;
	private final Supplier<MutableComponent> label;
	
	LogicReaderMode(boolean readsSignal, Supplier<MutableComponent> label)
	{
		this.readsSignal = readsSignal;
		this.label = label;
	}
	
	public boolean readsSignal()
	{
		return readsSignal;
	}
	
	@Override
	public MutableComponent label()
	{
		return label.get();
	}
}
