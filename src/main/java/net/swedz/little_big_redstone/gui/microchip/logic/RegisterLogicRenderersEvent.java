package net.swedz.little_big_redstone.gui.microchip.logic;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;

import java.util.Map;

public final class RegisterLogicRenderersEvent extends Event implements IModBusEvent
{
	private final Map<ResourceLocation, LogicRendererProvider<?, ?>> map;
	
	public RegisterLogicRenderersEvent(
			Map<ResourceLocation, LogicRendererProvider<?, ?>> map
	)
	{
		this.map = map;
	}
	
	public void register(
			ResourceLocation id,
			LogicRendererProvider provider
	)
	{
		if(map.put(id, provider) != null)
		{
			throw new IllegalStateException("Logic renderer for logic type id " + id + " already registered");
		}
	}
	
	public void register(
			DeferredHolder<LogicType, LogicType> type,
			LogicRendererProvider provider
	)
	{
		this.register(type.getId(), provider);
	}
}
