package net.swedz.little_big_redstone.gui.microchip.logic;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.client.model.logic.LogicBakingModelData;
import net.swedz.little_big_redstone.client.model.logic.LogicModelColorSet;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicGridSize;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

import java.util.Optional;
import java.util.function.Function;

public abstract class LogicRenderer<L extends LogicComponent<L, C>, C extends LogicConfig>
{
	public static final ResourceLocation PORT_INPUT  = LBR.id("textures/logic/port_input.png");
	public static final ResourceLocation PORT_OUTPUT = LBR.id("textures/logic/port_output.png");
	
	public abstract void render(Context context, TesseractGuiGraphics graphics, L component, int x, int y);
	
	protected void renderPort(TesseractGuiGraphics graphics, int x, int y, LogicGridSize size, boolean input, int index, int maxPorts, float red, float green, float blue, float alpha)
	{
		ResourceLocation texture = input ? PORT_INPUT : PORT_OUTPUT;
		
		int renderX = size.portTopLeftCornerX(x, input, index, maxPorts);
		int renderY = size.portTopLeftCornerY(y, input, index, maxPorts);
		
		graphics.setTexture(texture);
		graphics.blit(renderX, renderY, 0, 0, 16, 16, 16, 16, red, green, blue, alpha);
	}
	
	protected void renderAllPorts(Context context, TesseractGuiGraphics graphics, int x, int y, L component, float red, float green, float blue)
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
	
	protected void renderGridBlock(TesseractGuiGraphics graphics, ResourceLocation texture, int x, int y, LogicGridSize size, int argb)
	{
		graphics.setColor(argb);
		graphics.setTexture(texture);
		if(size.isSingle())
		{
			graphics.blit(x, y, 0, 0, 16, 16, 16, 16);
		}
		else
		{
			graphics.nineSlice(x, y, size.widthPixels(), size.heightPixels(), 16, 16, 3);
		}
		graphics.resetColor();
	}
	
	protected void renderBackground(TesseractGuiGraphics graphics, ResourceLocation background, ResourceLocation border, int x, int y, LogicGridSize size, int foregroundColor, int backgroundColor)
	{
		this.renderGridBlock(graphics, background, x, y, size, backgroundColor);
		this.renderGridBlock(graphics, border, x, y, size, foregroundColor);
	}
	
	protected void renderBackground(Context context, TesseractGuiGraphics graphics, int x, int y, LogicComponent component)
	{
		this.renderBackground(
				graphics,
				context.getTexture("background"),
				context.getTexture("border"),
				x, y, component.size(),
				context.foregroundColor(), context.backgroundColor()
		);
	}
	
	protected void renderInvalidOverlay(TesseractGuiGraphics graphics, int x, int y, LogicGridSize size)
	{
		graphics.setTexture(LBR.id("textures/logic/misconfigured.png"));
		graphics.blit(x + size.widthPixels() - 7 + 1, y - 1, 0, 0, 7, 7, 7, 7);
	}
	
	public record Context(
			LogicModelColorSet colorPalette,
			Function<String, ResourceLocation> textureGetter,
			boolean showPorts, boolean hasSelectedPort, boolean isCarryingWire
	)
	{
		public static Context create(Optional<DyeColor> logicColor, DyeColor menuColor, LogicType type, boolean showPorts, boolean hasSelectedPort, boolean isCarryingWire)
		{
			var modelData = LogicBakingModelData.get(type);
			return new Context(
					modelData.getColorSet(logicColor, menuColor),
					modelData::getBoardTextureLocation,
					showPorts,
					hasSelectedPort,
					isCarryingWire
			);
		}
		
		public static Context create(DyeColor logicColor, DyeColor menuColor, LogicType type, boolean showPorts, boolean hasSelectedPort, boolean isCarryingWire)
		{
			return create(Optional.ofNullable(logicColor), menuColor, type, showPorts, hasSelectedPort, isCarryingWire);
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
