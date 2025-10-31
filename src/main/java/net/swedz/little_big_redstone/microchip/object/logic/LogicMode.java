package net.swedz.little_big_redstone.microchip.object.logic;

import net.minecraft.network.chat.MutableComponent;
import net.swedz.little_big_redstone.LBR;

public interface LogicMode
{
	MutableComponent label();
	
	static LogicMode input()
	{
		return () -> LBR.text().input();
	}
	
	static LogicMode output()
	{
		return () -> LBR.text().output();
	}
}
