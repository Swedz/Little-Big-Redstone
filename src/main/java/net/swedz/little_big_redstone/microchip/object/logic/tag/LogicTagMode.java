package net.swedz.little_big_redstone.microchip.object.logic.tag;

import net.minecraft.network.chat.MutableComponent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicMode;

import java.util.function.Supplier;

public enum LogicTagMode implements LogicMode
{
	SENSOR(() -> LBR.text().sensor()),
	EMITTER(() -> LBR.text().emitter());
	
	private final Supplier<MutableComponent> label;
	
	LogicTagMode(Supplier<MutableComponent> label)
	{
		this.label = label;
	}
	
	@Override
	public MutableComponent label()
	{
		return label.get();
	}
}
