package net.swedz.little_big_redstone.microchip.object.logic.config.menu;

import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;

public abstract class LogicConfigMenuProvider<C extends LogicConfig<C>>
{
	protected final C config;
	
	public LogicConfigMenuProvider(C config)
	{
		this.config = config;
	}
	
	public abstract void create(LogicConfigMenuBuilder builder, int width, int height);
}
