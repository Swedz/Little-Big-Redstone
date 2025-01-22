package net.swedz.little_big_redstone.gui.microchip.logic;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.helper.GuiGraphicsHelper;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.logic.LogicGridSize;

public abstract class LogicRenderer<L extends LogicComponent>
{
	public static final ResourceLocation BACKGROUND         = LBR.id("textures/logic/background.png");
	public static final ResourceLocation BACKGROUND_OVERLAY = LBR.id("textures/logic/background_overlay.png");
	
	public static final ResourceLocation BACKGROUND_CIRCLE         = LBR.id("textures/logic/background_circle.png");
	public static final ResourceLocation BACKGROUND_CIRCLE_OVERLAY = LBR.id("textures/logic/background_circle_overlay.png");
	
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
	
	protected void renderGridBlock(GuiGraphics graphics, ResourceLocation texture, int x, int y, LogicGridSize size, float red, float green, float blue)
	{
		graphics.setColor(red, green, blue, 1);
		
		if(size.isSingle())
		{
			graphics.blit(texture, x, y, 0, 0, 16, 16, 16, 16);
		}
		else
		{
			GuiGraphicsHelper.nineSlice(graphics, texture, x, y, size.widthPixels(), size.heightPixels(), 16, 16, 3);
		}
		
		graphics.setColor(1, 1, 1, 1);
	}
	
	protected void renderBackground(GuiGraphics graphics, int x, int y, LogicGridSize size, float red, float green, float blue)
	{
		this.renderGridBlock(graphics, BACKGROUND, x, y, size, red, green, blue);
		this.renderGridBlock(graphics, BACKGROUND_OVERLAY, x, y, size, 1, 1, 1);
	}
	
	protected void renderBackgroundCircle(GuiGraphics graphics, int x, int y, float red, float green, float blue)
	{
		var size = new LogicGridSize(1, 1);
		this.renderGridBlock(graphics, BACKGROUND_CIRCLE, x, y, size, red, green, blue);
		this.renderGridBlock(graphics, BACKGROUND_CIRCLE_OVERLAY, x, y, size, 1, 1, 1);
	}
	
	public record Context(boolean isCarried, boolean hasSelectedPort, boolean isCarryingWire)
	{
	}
}
