package net.swedz.little_big_redstone.gui.microchip.logic;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.swedz.little_big_redstone.gui.microchip.logic.renderer.CalculatorLogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.renderer.IORenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.renderer.OnOffLogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.renderer.SequencerRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.renderer.SimpleLogicRenderer;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

import java.util.Map;

public final class LogicRenderers
{
	private static final Map<LogicType<?>, LogicRendererProvider<?>> PROVIDERS = Maps.newConcurrentMap();
	
	private static Map<LogicType<?>, LogicRenderer<?>> RENDERERS = Map.of();
	
	static
	{
		register(LogicTypes.DEBUGGER, SimpleLogicRenderer::new);
		
		register(LogicTypes.IO, IORenderer::new);
		register(LogicTypes.READER, SimpleLogicRenderer::new);
		register(LogicTypes.TAG, SimpleLogicRenderer::new);
		
		register(LogicTypes.NOT, SimpleLogicRenderer::new);
		register(LogicTypes.AND, SimpleLogicRenderer::new);
		register(LogicTypes.NAND, SimpleLogicRenderer::new);
		register(LogicTypes.OR, SimpleLogicRenderer::new);
		register(LogicTypes.NOR, SimpleLogicRenderer::new);
		register(LogicTypes.XOR, SimpleLogicRenderer::new);
		
		register(LogicTypes.SEQUENCER, SequencerRenderer::new);
		register(LogicTypes.PULSE_THROTTLER, SimpleLogicRenderer::new);
		register(LogicTypes.SELECTOR, SimpleLogicRenderer::new);
		register(LogicTypes.RANDOMIZER, SimpleLogicRenderer::new);
		register(LogicTypes.COMPARATOR, SimpleLogicRenderer::new);
		register(LogicTypes.CALCULATOR, CalculatorLogicRenderer::new);
		
		register(LogicTypes.T_FLIP_FLOP, OnOffLogicRenderer::new);
		register(LogicTypes.RS_NOR_LATCH, OnOffLogicRenderer::new);
	}
	
	public static void init()
	{
		RENDERERS = createRenderers();
	}
	
	private static <L extends LogicComponent> void register(LogicType<L> type, LogicRendererProvider<L> provider)
	{
		PROVIDERS.put(type, provider);
	}
	
	private static Map<LogicType<?>, LogicRenderer<?>> createRenderers()
	{
		ImmutableMap.Builder<LogicType<?>, LogicRenderer<?>> builder = ImmutableMap.builder();
		PROVIDERS.forEach((type, provider) ->
		{
			try
			{
				builder.put(type, provider.create());
			}
			catch (Exception ex)
			{
				throw new IllegalStateException("Failed to create logic renderer for " + type.id(), ex);
			}
		});
		return builder.build();
	}
	
	public static <L extends LogicComponent> void render(LogicRenderer.Context context, GuiGraphicsExtractor graphics, L component, int x, int y)
	{
		var renderer = (LogicRenderer<L>) RENDERERS.get(component.type());
		if(renderer != null)
		{
			renderer.render(context, graphics, component, x, y);
		}
	}
}
