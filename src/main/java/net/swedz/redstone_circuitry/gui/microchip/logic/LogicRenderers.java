package net.swedz.redstone_circuitry.gui.microchip.logic;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Unit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.swedz.redstone_circuitry.RedstoneCircuitry;
import net.swedz.redstone_circuitry.gui.microchip.logic.renderer.LogicGateRenderer;
import net.swedz.redstone_circuitry.gui.microchip.logic.renderer.SequencerRenderer;
import net.swedz.redstone_circuitry.microchip.logic.Logic;
import net.swedz.redstone_circuitry.microchip.logic.LogicType;
import net.swedz.redstone_circuitry.microchip.logic.LogicTypes;

import java.util.Map;

@EventBusSubscriber(modid = RedstoneCircuitry.ID, value = Dist.CLIENT)
public final class LogicRenderers
{
	private static final Map<LogicType<?>, LogicRendererProvider<?>> PROVIDERS = Maps.newConcurrentMap();
	
	private static Map<LogicType<?>, LogicRenderer<?>> RENDERERS = Map.of();
	
	static
	{
		register(LogicTypes.NOT, LogicGateRenderer::new);
		register(LogicTypes.AND, LogicGateRenderer::new);
		register(LogicTypes.NAND, LogicGateRenderer::new);
		register(LogicTypes.OR, LogicGateRenderer::new);
		register(LogicTypes.NOR, LogicGateRenderer::new);
		register(LogicTypes.XOR, LogicGateRenderer::new);
		
		register(LogicTypes.SEQUENCER, SequencerRenderer::new);
	}
	
	private static <L extends Logic> void register(LogicType<L> type, LogicRendererProvider<L> provider)
	{
		PROVIDERS.put(type, provider);
	}
	
	private static Map<LogicType<?>, LogicRenderer<?>> createRenderers(LogicRendererProvider.Context context)
	{
		ImmutableMap.Builder<LogicType<?>, LogicRenderer<?>> builder = ImmutableMap.builder();
		PROVIDERS.forEach((type, provider) ->
		{
			try
			{
				builder.put(type, provider.create(context));
			}
			catch (Exception ex)
			{
				throw new IllegalStateException("Failed to create logic renderer for " + type.id(), ex);
			}
		});
		return builder.build();
	}
	
	@SubscribeEvent
	private static void addReloadListeners(AddReloadListenerEvent event)
	{
		event.addListener((stage, manager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) ->
				stage.wait(Unit.INSTANCE).thenRunAsync(() ->
				{
					var context = new LogicRendererProvider.Context();
					RENDERERS = createRenderers(context);
				}));
	}
	
	public static <L extends Logic> void render(GuiGraphics graphics, L logic, int x, int y)
	{
		var renderer = (LogicRenderer<L>) RENDERERS.get(logic.type());
		if(renderer != null)
		{
			renderer.render(graphics, logic, x, y);
		}
	}
}
