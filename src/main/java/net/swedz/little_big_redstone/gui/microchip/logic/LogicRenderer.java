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
import net.swedz.little_big_redstone.microchip.object.logic.LogicPortHolder;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;

import java.util.Optional;
import java.util.function.Function;

public abstract class LogicRenderer<L extends LogicComponent<L, C>, C extends LogicConfig>
{
	public static final Identifier PORT_INPUT  = LBR.id("logic/port_input");
	public static final Identifier PORT_OUTPUT = LBR.id("logic/port_output");
	
	public abstract void render(Context context, GuiGraphicsExtractor graphics, L component, int x, int y);
	
	protected void renderPort(GuiGraphicsExtractor graphics, int x, int y, LogicGridSize size, boolean input, int index, int maxPorts, int color)
	{
		var texture = input ? PORT_INPUT : PORT_OUTPUT;
		
		int renderX = size.portTopLeftCornerX(x, input, index, maxPorts);
		int renderY = size.portTopLeftCornerY(y, input, index, maxPorts);
		
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, renderX, renderY, 16, 16, color);
	}
	
	protected void renderAllPorts(Context context, GuiGraphicsExtractor graphics, int x, int y, LogicPortHolder portHolder)
	{
		if(context.showPorts())
		{
			var size = portHolder.size();
			
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
			int inputColor = ARGB.colorFromFloat(inputAlpha, 1, 1, 1);
			int outputColor = ARGB.colorFromFloat(outputAlpha, 1, 1, 1);
			
			int inputs = portHolder.inputPorts();
			for(int i = 0; i < inputs; i++)
			{
				this.renderPort(graphics, x, y, size, true, i, inputs, inputColor);
			}
			
			int outputs = portHolder.outputPorts();
			for(int i = 0; i < outputs; i++)
			{
				this.renderPort(graphics, x, y, size, false, i, outputs, outputColor);
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
	
	protected void renderBackground(Context context, GuiGraphicsExtractor graphics, int x, int y, LogicPortHolder portHolder)
	{
		this.renderBackground(
				graphics,
				context.getTexture("background"),
				context.getTexture("border"),
				x,
				y,
				portHolder.size(),
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
		public static Context create(Optional<DyeColor> logicColor, DyeColor menuColor, LogicType type, boolean showPorts, boolean hasSelectedPort, boolean isCarryingWire)
		{
			var model = LogicItemModel.get(type);
			return new Context(
					model.colorPalette().getColorSet(logicColor, menuColor),
					model.boardTextures()::get,
					showPorts,
					hasSelectedPort,
					isCarryingWire
			);
		}
		
		public static Context create(DyeColor logicColor, DyeColor menuColor, LogicType type, boolean showPorts, boolean hasSelectedPort, boolean isCarryingWire)
		{
			return create(Optional.ofNullable(logicColor), menuColor, type, showPorts, hasSelectedPort, isCarryingWire);
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
