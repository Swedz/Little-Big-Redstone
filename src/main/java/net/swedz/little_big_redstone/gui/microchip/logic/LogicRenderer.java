package net.swedz.little_big_redstone.gui.microchip.logic;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.client.model.logic.LogicBakingModelData;
import net.swedz.little_big_redstone.client.model.logic.LogicModelColorSet;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicGridSize;
import net.swedz.tesseract.neoforge.helper.gui.ExtraGuiGraphics;

import java.util.function.Function;

public abstract class LogicRenderer<L extends LogicComponent>
{
	public static final Identifier BORDER_SQUARE     = LBR.id("textures/logic/border_square.png");
	public static final Identifier BACKGROUND_SQUARE = LBR.id("textures/logic/background_square.png");
	
	public static final Identifier BORDER_CIRCLE     = LBR.id("textures/logic/border_circle.png");
	public static final Identifier BACKGROUND_CIRCLE = LBR.id("textures/logic/background_circle.png");
	
	public static final Identifier PORT_INPUT  = LBR.id("textures/logic/port_input.png");
	public static final Identifier PORT_OUTPUT = LBR.id("textures/logic/port_output.png");
	
	public abstract void render(Context context, GuiGraphicsExtractor graphics, L component, int x, int y);
	
	protected void renderPort(GuiGraphicsExtractor graphics, int x, int y, LogicGridSize size, boolean input, int index, int maxPorts, float red, float green, float blue, float alpha)
	{
		Identifier texture = input ? PORT_INPUT : PORT_OUTPUT;
		
		int renderX = size.portTopLeftCornerX(x, input, index, maxPorts);
		int renderY = size.portTopLeftCornerY(y, input, index, maxPorts);
		
		int color = ARGB.colorFromFloat(alpha, red, green, blue);
		graphics.blit(texture, renderX, renderY, 0, 0, 16, 16, 16, 16, color);
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
		if(size.isSingle())
		{
			graphics.blit(texture, x, y, 0, 0, 16, 16, 16, 16, argb);
		}
		else
		{
			ExtraGuiGraphics.nineSlice(graphics, RenderPipelines.GUI_TEXTURED, texture, argb, 0xFFFFFFFF, x, y, size.widthPixels(), size.heightPixels(), 16, 16, 3);
		}
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
				x, y, component.size(),
				context.foregroundColor(), context.backgroundColor()
		);
	}
	
	protected void renderInvalidOverlay(GuiGraphicsExtractor graphics, int x, int y, LogicGridSize size)
	{
		graphics.blit(LBR.id("textures/logic/misconfigured.png"), x + size.widthPixels() - 7 + 1, y - 1, 0, 0, 7, 7, 7, 7);
	}
	
	public record Context(
			LogicModelColorSet colorPalette,
			Function<String, Identifier> textureGetter,
			boolean showPorts, boolean hasSelectedPort, boolean isCarryingWire
	)
	{
		public static Context create(DyeColor menuColor, LogicComponent<?, ?> component, boolean showPorts, boolean hasSelectedPort, boolean isCarryingWire)
		{
			var modelData = LogicBakingModelData.get(component);
			return new Context(modelData.getColorSet(component, menuColor), modelData::getBoardTextureLocation, showPorts, hasSelectedPort, isCarryingWire);
		}
		
		public Identifier getTexture(String key)
		{
			return textureGetter.apply(key);
		}
		
		public int foregroundColor()
		{
			return colorPalette.foreground();
		}
		
		public int backgroundColor()
		{
			return colorPalette.background();
		}
	}
}
