package net.swedz.little_big_redstone.gui.microchip.logic;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.client.model.logic.LogicItemModel;
import net.swedz.little_big_redstone.client.model.logic.LogicModelColorSet;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicGridSize;

import java.util.function.Function;

public abstract class LogicRenderer<L extends LogicComponent>
{
	public static final Identifier PORT_INPUT  = LBR.id("logic/port_input");
	public static final Identifier PORT_OUTPUT = LBR.id("logic/port_output");
	
	public abstract void render(Context context, GuiGraphicsExtractor graphics, L component, int x, int y);
	
	protected void renderPort(GuiGraphicsExtractor graphics, int x, int y, LogicGridSize size, boolean input, int index, int maxPorts, float red, float green, float blue, float alpha)
	{
		Identifier texture = input ? PORT_INPUT : PORT_OUTPUT;
		
		int renderX = size.portTopLeftCornerX(x, input, index, maxPorts);
		int renderY = size.portTopLeftCornerY(y, input, index, maxPorts);
		
		int color = ARGB.colorFromFloat(alpha, red, green, blue);
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, renderX, renderY, 16, 16, color);
	}
	
	protected void renderAllPorts(Context context, GuiGraphicsExtractor graphics, int x, int y, L component, float red, float green, float blue)
	{
		if(context.showPorts())
		{
			var size = component.size();
			
			float inputAlpha = 0.5f;
			float outputAlpha = 0.5f;
			if(context.hasSelectedPort())
			{
				inputAlpha = 1;
			}
			else if(context.isCarryingWire())
			{
				outputAlpha = 1;
			}
			
			int inputs = component.inputs();
			for(int i = 0; i < inputs; i++)
			{
				this.renderPort(graphics, x, y, size, true, i, inputs, red, green, blue, inputAlpha);
			}
			
			int outputs = component.outputs();
			for(int i = 0; i < outputs; i++)
			{
				this.renderPort(graphics, x, y, size, false, i, outputs, red, green, blue, outputAlpha);
			}
		}
	}
	
	protected void renderGridBlock(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, LogicGridSize size, int argb)
	{
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, size.widthPixels(), size.heightPixels(), argb);
	}
	
	protected void renderBackground(GuiGraphicsExtractor graphics, Identifier background, Identifier border, int x, int y, LogicGridSize size, int foregroundColor, int backgroundColor)
	{
		this.renderGridBlock(graphics, background, x, y, size, backgroundColor);
		this.renderGridBlock(graphics, border, x, y, size, foregroundColor);
	}
	
	protected void renderBackground(Context context, GuiGraphicsExtractor graphics, int x, int y, LogicComponent component)
	{
		this.renderBackground(
				graphics,
				context.getTexture("background"),
				context.getTexture("border"),
				x,
				y,
				component.size(),
				context.foregroundColor(),
				context.backgroundColor()
		);
	}
	
	protected void renderInvalidOverlay(GuiGraphicsExtractor graphics, int x, int y, LogicGridSize size)
	{
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LBR.id("logic/misconfigured"), x + size.widthPixels() - 7 + 1, y - 1, 7, 7);
	}
	
	public record Context(
			LogicModelColorSet colorSet,
			Function<String, Identifier> textureGetter,
			boolean showPorts,
			boolean hasSelectedPort,
			boolean isCarryingWire
	)
	{
		public static Context create(DyeColor menuColor, LogicComponent<?, ?> component, boolean showPorts, boolean hasSelectedPort, boolean isCarryingWire)
		{
			var model = LogicItemModel.get(component);
			return new Context(
					model.colorPalette().getColorSet(component, menuColor),
					model.boardTextures()::get,
					showPorts,
					hasSelectedPort,
					isCarryingWire
			);
		}
		
		public Identifier getTexture(String key)
		{
			return textureGetter.apply(key);
		}
		
		public int foregroundColor()
		{
			return colorSet.foreground();
		}
		
		public int backgroundColor()
		{
			return colorSet.background();
		}
	}
}
