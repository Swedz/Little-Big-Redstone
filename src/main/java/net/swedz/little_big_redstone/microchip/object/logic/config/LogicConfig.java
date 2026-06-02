package net.swedz.little_big_redstone.microchip.object.logic.config;

import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponents;
import net.swedz.little_big_redstone.microchip.object.logic.LogicPortHolder;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;

import java.util.List;

public interface LogicConfig<C extends LogicConfig<C>> extends LogicPortHolder
{
	LogicType<?, C> type();
	
	default boolean checkValid(LogicComponents components)
	{
		return true;
	}
	
	default void appendNoShiftHoverText(List<Component> lines)
	{
	}
	
	default void appendShiftHoverText(List<Component> lines)
	{
	}
	
	default void appendConfigHoverText(List<Component> lines)
	{
	}
	
	default boolean hasMenu()
	{
		return false;
	}
	
	default LogicConfigMenuProvider<C> getMenuProvider()
	{
		throw new UnsupportedOperationException("LogicConfig#getMenuProvider must be implemented when LogicConfig#hasMenu returns true");
	}
}