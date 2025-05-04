package net.swedz.little_big_redstone.helper.guigraphics;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public interface TooltipGuiGraphics extends TextGuiGraphics, SizedGuiGraphics
{
	int DEFAULT_TOOLTIP_BACKGROUND_COLOR    = 0xF0100010;
	int DEFAULT_TOOLTIP_BORDER_TOP_COLOR    = 0x505000FF;
	int DEFAULT_TOOLTIP_BORDER_BOTTOM_COLOR = 0x5028007F;
	
	//
	
	default void renderTooltip(List<Component> lines, int x, int y)
	{
		this.renderTooltip(lines, x, y, this.guiWidth());
	}
	
	default void renderTooltip(List<Component> lines, int x, int y, int backgroundTopColor, int backgroundBottomColor, int borderTopColor, int borderBottomColor)
	{
		this.renderTooltip(lines, x, y, this.guiWidth(), backgroundTopColor, backgroundBottomColor, borderTopColor, borderBottomColor);
	}
	
	//
	
	default void renderTooltip(List<Component> lines, int x, int y, int maxWidth)
	{
		this.renderTooltip(lines, x, y, maxWidth, DEFAULT_TOOLTIP_BACKGROUND_COLOR, DEFAULT_TOOLTIP_BACKGROUND_COLOR, DEFAULT_TOOLTIP_BORDER_TOP_COLOR, DEFAULT_TOOLTIP_BORDER_BOTTOM_COLOR);
	}
	
	default void renderTooltip(List<Component> lines, int x, int y, int maxWidth, int backgroundTopColor, int backgroundBottomColor, int borderTopColor, int borderBottomColor)
	{
		int width = 0;
		for(var line : lines)
		{
			var clientComponent = ClientTooltipComponent.create(line.getVisualOrderText());
			int lineWidth = clientComponent.getWidth(this.getFont());
			if(lineWidth > width)
			{
				width = lineWidth;
			}
		}
		if(x + width > maxWidth)
		{
			width = maxWidth - x - 6;
		}
		
		List<ClientTooltipComponent> splitLines = Lists.newArrayList();
		for(var line : lines)
		{
			if(line.equals(Component.empty()))
			{
				splitLines.add(ClientTooltipComponent.create(line.getVisualOrderText()));
				continue;
			}
			for(var splitLine : this.getFont().split(line, width))
			{
				splitLines.add(ClientTooltipComponent.create(splitLine));
			}
		}
		
		int height = 0;
		for(var line : splitLines)
		{
			height += line.getHeight();
		}
		
		this.renderTooltipInternal(splitLines, x, y, width, height, backgroundTopColor, backgroundBottomColor, borderTopColor, borderBottomColor);
	}
	
	@ApiStatus.Internal
	void renderTooltipInternal(List<ClientTooltipComponent> lines, int x, int y, int width, int height, int backgroundTopColor, int backgroundBottomColor, int borderTopColor, int borderBottomColor);
}
