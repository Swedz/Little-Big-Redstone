package net.swedz.little_big_redstone.gui.microchip.logic;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.gui.microchip.logic.renderer.CalculatorLogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.renderer.IORenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.renderer.OnOffLogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.renderer.SequencerRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.renderer.SimpleLogicRenderer;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

import java.util.Map;

public final class LogicRenderers
{
	private static final Map<ResourceLocation, LogicRendererProvider<?, ?>> PROVIDERS = Maps.newConcurrentMap();
	
	private static Map<ResourceLocation, LogicRenderer<?, ?>> RENDERERS = Map.of();
	
	static
	{
		register(LBRLogicTypes.DEBUGGER, SimpleLogicRenderer::new);
		
		register(LBRLogicTypes.IO, IORenderer::new);
		register(LBRLogicTypes.READER, SimpleLogicRenderer::new);
		register(LBRLogicTypes.TAG, SimpleLogicRenderer::new);
		
		register(LBRLogicTypes.NOT, SimpleLogicRenderer::new);
		register(LBRLogicTypes.AND, SimpleLogicRenderer::new);
		register(LBRLogicTypes.NAND, SimpleLogicRenderer::new);
		register(LBRLogicTypes.OR, SimpleLogicRenderer::new);
		register(LBRLogicTypes.NOR, SimpleLogicRenderer::new);
		register(LBRLogicTypes.XOR, SimpleLogicRenderer::new);
		
		register(LBRLogicTypes.SEQUENCER, SequencerRenderer::new);
		register(LBRLogicTypes.PULSE_THROTTLER, SimpleLogicRenderer::new);
		register(LBRLogicTypes.SELECTOR, SimpleLogicRenderer::new);
		register(LBRLogicTypes.RANDOMIZER, SimpleLogicRenderer::new);
		register(LBRLogicTypes.COMPARATOR, SimpleLogicRenderer::new);
		register(LBRLogicTypes.CALCULATOR, CalculatorLogicRenderer::new);
		
		register(LBRLogicTypes.T_FLIP_FLOP, OnOffLogicRenderer::new);
		register(LBRLogicTypes.RS_NOR_LATCH, OnOffLogicRenderer::new);
	}
	
	public static void init()
	{
		RENDERERS = createRenderers();
	}
	
	private static void register(
			DeferredHolder<LogicType, LogicType> type,
			LogicRendererProvider provider
	)
	{
		PROVIDERS.put(type.getId(), provider);
	}
	
	private static Map<ResourceLocation, LogicRenderer<?, ?>> createRenderers()
	{
		ImmutableMap.Builder<ResourceLocation, LogicRenderer<?, ?>> builder = ImmutableMap.builder();
		PROVIDERS.forEach((id, provider) ->
		{
			try
			{
				builder.put(id, provider.create());
			}
			catch (Exception ex)
			{
				throw new IllegalStateException("Failed to create logic renderer for " + id, ex);
			}
		});
		return builder.build();
	}
	
	public static void render(LogicRenderer.Context context, TesseractGuiGraphics graphics, LogicComponent<?, ?> component, int x, int y)
	{
		LogicRenderer renderer = RENDERERS.get(component.type().id());
		if(renderer != null)
		{
			renderer.render(context, graphics, component, x, y);
		}
	}
}
