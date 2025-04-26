package net.swedz.little_big_redstone.gui.microchip.logic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.client.model.logic.LogicBakedModel;
import net.swedz.little_big_redstone.client.model.logic.LogicModelColorSet;
import net.swedz.little_big_redstone.helper.GuiGraphicsHelper;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.logic.LogicGridSize;

import java.util.function.Function;

public abstract class LogicRenderer<L extends LogicComponent>
{
	public static final ResourceLocation BORDER_SQUARE     = LBR.id("textures/logic/border_square.png");
	public static final ResourceLocation BACKGROUND_SQUARE = LBR.id("textures/logic/background_square.png");
	
	public static final ResourceLocation BORDER_CIRCLE     = LBR.id("textures/logic/border_circle.png");
	public static final ResourceLocation BACKGROUND_CIRCLE = LBR.id("textures/logic/background_circle.png");
	
	public static final ResourceLocation PORT_INPUT  = LBR.id("textures/logic/port_input.png");
	public static final ResourceLocation PORT_OUTPUT = LBR.id("textures/logic/port_output.png");
	
	public LogicRenderer(LogicRendererProvider.Context context)
	{
	}
	
	public abstract void render(Context context, GuiGraphics graphics, L component, int x, int y);
	
	protected void renderPort(GuiGraphics graphics, int x, int y, LogicGridSize size, boolean input, int index, int maxPorts, float red, float green, float blue, float alpha)
	{
		ResourceLocation texture = input ? PORT_INPUT : PORT_OUTPUT;
		
		int renderX = size.portTopLeftCornerX(x, input, index, maxPorts);
		int renderY = size.portTopLeftCornerY(y, input, index, maxPorts);
		
		GuiGraphicsHelper.blit(graphics, texture, renderX, renderY, 0, 0, 16, 16, 16, 16, red, green, blue, alpha);
	}
	
	protected void renderAllPorts(Context context, GuiGraphics graphics, int x, int y, L component, float red, float green, float blue)
	{
		if(!context.isCarried())
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
	
	protected void renderGridBlock(GuiGraphics graphics, ResourceLocation texture, int x, int y, LogicGridSize size, int argb)
	{
		GuiGraphicsHelper.setColor(graphics, argb);
		
		if(size.isSingle())
		{
			graphics.blit(texture, x, y, 0, 0, 16, 16, 16, 16);
		}
		else
		{
			GuiGraphicsHelper.nineSlice(graphics, texture, x, y, size.widthPixels(), size.heightPixels(), 16, 16, 3);
		}
		
		GuiGraphicsHelper.resetColor(graphics);
	}
	
	protected void renderBackground(GuiGraphics graphics, ResourceLocation background, ResourceLocation border, int x, int y, LogicGridSize size, int foregroundColor, int backgroundColor)
	{
		this.renderGridBlock(graphics, background, x, y, size, backgroundColor);
		this.renderGridBlock(graphics, border, x, y, size, foregroundColor);
	}
	
	protected void renderBackground(Context context, GuiGraphics graphics, int x, int y, LogicComponent component)
	{
		this.renderBackground(
				graphics,
				context.getTexture("background"),
				context.getTexture("border"),
				x, y, component.size(),
				context.foregroundColor(), context.backgroundColor()
		);
	}
	
	protected void renderInvalidOverlay(GuiGraphics graphics, int x, int y, LogicGridSize size)
	{
		graphics.blit(LBR.id("textures/logic/misconfigured.png"), x + size.widthPixels() - 7 + 1, y - 1, 0, 0, 7, 7, 7, 7);
	}
	
	public record Context(
			LogicModelColorSet colorPalette,
			Function<String, ResourceLocation> textureGetter,
			boolean isCarried, boolean hasSelectedPort, boolean isCarryingWire
	)
	{
		public static Context create(DyeColor menuColor, LogicComponent<?, ?> component, boolean isCarried, boolean hasSelectedPort, boolean isCarryingWire)
		{
			var type = component.type();
			var modelData = ((LogicBakedModel) Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.inventory(LBR.id(type.id())))).getData();
			var color = (DyeColor) component.color().orElse(menuColor);
			var colorPalette = modelData.getColorSet(color);
			return new Context(colorPalette, modelData::getBoardTextureLocation, isCarried, hasSelectedPort, isCarryingWire);
		}
		
		public ResourceLocation getTexture(String key)
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
