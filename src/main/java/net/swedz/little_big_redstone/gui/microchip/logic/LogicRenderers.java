package net.swedz.little_big_redstone.gui.microchip.logic;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.tesseract.api.Assert;

import java.util.Map;

public final class LogicRenderers
{
	private static Map<Identifier, LogicRenderer<?, ?>> RENDERERS = Map.of();
	
	private static boolean INITIALIZED = false;
	
	public static void setup(IEventBus bus)
	{
		Assert.that(!INITIALIZED, "Logic renderers have already been initialized");
		
		INITIALIZED = true;
		
		Map<Identifier, LogicRendererProvider<?, ?>> providers = Maps.newHashMap();
		ModLoader.postEvent(new RegisterLogicRenderersEvent(providers));
		RENDERERS = createRenderers(providers);
		
		bus.addListener(FMLCommonSetupEvent.class, LogicRenderers::validateRenderers);
	}
	
	private static void validateRenderers(FMLCommonSetupEvent event)
	{
		for(var type : LogicTypes.REGISTRY)
		{
			if(!RENDERERS.containsKey(type.id()))
			{
				throw new IllegalStateException("Missing logic renderer for logic type " + type.id());
			}
		}
		
		for(var entry : RENDERERS.entrySet())
		{
			if(!LogicTypes.REGISTRY.containsKey(entry.getKey()))
			{
				throw new IllegalStateException("Registered logic renderer " + entry.getKey() + " without an existing logic type for the same id");
			}
		}
	}
	
	private static Map<Identifier, LogicRenderer<?, ?>> createRenderers(
			Map<Identifier, LogicRendererProvider<?, ?>> providers
	)
	{
		ImmutableMap.Builder<Identifier, LogicRenderer<?, ?>> builder = ImmutableMap.builder();
		providers.forEach((id, provider) ->
		{
			try
			{
				builder.put(id, provider.create());
			}
			catch(Exception ex)
			{
				throw new IllegalStateException("Failed to create logic renderer for " + id, ex);
			}
		});
		return builder.build();
	}
	
	public static void render(LogicRenderer.Context context, GuiGraphicsExtractor graphics, LogicComponent<?, ?> component, int x, int y)
	{
		LogicRenderer renderer = RENDERERS.get(component.type().id());
		if(renderer != null)
		{
			renderer.render(context, graphics, component, x, y);
		}
	}
}
