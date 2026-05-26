package net.swedz.little_big_redstone.gui.microchip.panel;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.gui.microchip.widget.MicrochipWidgetContext;
import net.swedz.little_big_redstone.microchip.Microchip;

import java.util.function.Supplier;

abstract class MicrochipRenderPanel
{
	protected final DyeColor  color;
	protected final Microchip microchip;
	
	private final Supplier<MicrochipWidgetContext> context;
	
	public MicrochipRenderPanel(DyeColor color, Microchip microchip, Supplier<MicrochipWidgetContext> context)
	{
		this.color = color;
		this.microchip = microchip;
		this.context = context;
	}
	
	public MicrochipRenderPanel(DyeColor color, Microchip microchip)
	{
		this(color, microchip, null);
	}
	
	protected MicrochipWidgetContext context()
	{
		return context == null ? null : context.get();
	}
	
	public abstract void render(GuiGraphicsExtractor graphics);
}
