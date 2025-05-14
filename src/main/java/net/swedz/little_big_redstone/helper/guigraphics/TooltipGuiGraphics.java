package net.swedz.little_big_redstone.helper.guigraphics;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public interface TooltipGuiGraphics extends ColoredGuiGraphics, StringGuiGraphics, SizedGuiGraphics, WrappedGuiGraphics
{
	boolean isTooltipFirstLinePadded();
	
	void setTooltipFirstLinePadded(boolean padded);
	
	//
	
	BackgroundPadding getTooltipBackgroundPadding();
	
	void setTooltipBackgroundPadding(BackgroundPadding padding);
	
	default void setTooltipBackgroundPadding(int top, int bottom, int left, int right)
	{
		this.setTooltipBackgroundPadding(new BackgroundPadding(top, bottom, left, right));
	}
	
	default void resetTooltipBackgroundPadding()
	{
		this.setTooltipBackgroundPadding(new BackgroundPadding());
	}
	
	//
	
	default void renderTooltip(List<Component> lines, int x, int y, ResourceLocation backgroundAsset, int textureWidth, int textureHeight, int border)
	{
		this.renderTooltipBounded(lines, x, y, 0, 0, this.guiWidth(), this.guiHeight(), backgroundAsset, textureWidth, textureHeight, border);
	}
	
	default void renderTooltip(List<Component> lines, int x, int y)
	{
		this.renderTooltipBounded(lines, x, y, 0, 0, this.guiWidth(), this.guiHeight());
	}
	
	default void renderTooltip(List<Component> lines, int x, int y, int backgroundTopColor, int backgroundBottomColor, int borderTopColor, int borderBottomColor)
	{
		this.renderTooltipBounded(lines, x, y, 0, 0, this.guiWidth(), this.guiHeight(), backgroundTopColor, backgroundBottomColor, borderTopColor, borderBottomColor);
	}
	
	//
	
	default void renderTooltipBounded(List<Component> lines, int x, int y, int minWidth, int minHeight, int maxWidth, int maxHeight, ResourceLocation backgroundAsset, int textureWidth, int textureHeight, int border)
	{
		this.renderTooltipBoundedInternal(lines, x, y, minWidth, minHeight, maxWidth, maxHeight, new NineSpliceBackground(this.internal(), this.getTooltipBackgroundPadding(), backgroundAsset, textureWidth, textureHeight, border));
	}
	
	default void renderTooltipBounded(List<Component> lines, int x, int y, int minWidth, int minHeight, int maxWidth, int maxHeight)
	{
		this.renderTooltipBoundedInternal(lines, x, y, minWidth, minHeight, maxWidth, maxHeight, new VanillaBackground(this.internal(), this.getTooltipBackgroundPadding()));
	}
	
	default void renderTooltipBounded(List<Component> lines, int x, int y, int minWidth, int minHeight, int maxWidth, int maxHeight, int backgroundTopColor, int backgroundBottomColor, int borderTopColor, int borderBottomColor)
	{
		this.renderTooltipBoundedInternal(lines, x, y, minWidth, minHeight, maxWidth, maxHeight, new VanillaBackground(this.internal(), this.getTooltipBackgroundPadding(), backgroundTopColor, backgroundBottomColor, borderTopColor, borderBottomColor));
	}
	
	//
	
	@ApiStatus.Internal
	default void renderTooltipBoundedInternal(List<Component> lines, int x, int y, int minWidth, int minHeight, int maxWidth, int maxHeight, Background background)
	{
		int width = 0;
		for(var line : lines)
		{
			int lineWidth = this.getFont().width(line);
			if(lineWidth > width)
			{
				width = lineWidth;
			}
		}
		if(x + width > maxWidth)
		{
			width = maxWidth - x - this.getTooltipBackgroundPadding().right() - 2;
		}
		if(width < minWidth)
		{
			width = minWidth;
		}
		
		List<FormattedCharSequence> splitLines = Lists.newArrayList();
		for(var line : lines)
		{
			if(line.equals(Component.empty()))
			{
				splitLines.add(line.getVisualOrderText());
				continue;
			}
			splitLines.addAll(this.getFont().split(line, width));
		}
		
		int height = 0;
		int lineIndex = 0;
		for(var line : splitLines)
		{
			height += 10 + (this.isTooltipFirstLinePadded() && lineIndex == 0 ? 2 : 0);
			lineIndex++;
		}
		if(y + height > maxHeight)
		{
			height = maxHeight - y - this.getTooltipBackgroundPadding().bottom() - 2;
		}
		if(height < minHeight)
		{
			height = minHeight;
		}
		
		this.renderTooltipInternal(splitLines, x, y, width, height, background);
	}
	
	@ApiStatus.Internal
	default void renderTooltipInternal(List<FormattedCharSequence> lines, int x, int y, int width, int height, Background background)
	{
		background.render(x, y, width, height);
		this.renderTooltipTextInternal(lines, x, y, width, height);
	}
	
	@ApiStatus.Internal
	default void renderTooltipTextInternal(List<FormattedCharSequence> lines, int x, int y, int width, int height)
	{
		this.internal().pose().pushPose();
		this.internal().pose().translate(0, 0, 400);
		
		int textHeight = 0;
		int lineIndex = 0;
		for(var line : lines)
		{
			int textY = y + textHeight;
			int lineHeight = 10 + (this.isTooltipFirstLinePadded() && lineIndex == 0 ? 2 : 0);
			if(textHeight + lineHeight > height)
			{
				this.getFont().drawInBatch(
						Component.literal("..."),
						x, textY,
						this.getColorARGB(),
						this.isStringDropShadow(),
						this.internal().pose().last().pose(), this.internal().bufferSource(),
						Font.DisplayMode.NORMAL,
						0,
						LightTexture.FULL_BRIGHT
				);
				break;
			}
			this.getFont().drawInBatch(
					line,
					x, textY,
					this.getColorARGB(),
					this.isStringDropShadow(),
					this.internal().pose().last().pose(), this.internal().bufferSource(),
					Font.DisplayMode.NORMAL,
					0,
					LightTexture.FULL_BRIGHT
			);
			textHeight += lineHeight;
			lineIndex++;
		}
		
		this.internal().pose().popPose();
	}
	
	//
	
	record BackgroundPadding(int top, int bottom, int left, int right)
	{
		public BackgroundPadding()
		{
			this(3, 3, 3, 3);
		}
	}
	
	abstract class Background implements WrappedGuiGraphics
	{
		protected final GuiGraphics internal;
		
		protected final BackgroundPadding padding;
		
		public Background(GuiGraphics internal,
						  BackgroundPadding padding)
		{
			this.internal = internal;
			this.padding = padding;
		}
		
		public abstract void render(int x, int y, int width, int height);
	}
	
	final class VanillaBackground extends Background
	{
		private final int backgroundTopColor;
		private final int backgroundBottomColor;
		private final int borderTopColor;
		private final int borderBottomColor;
		
		private VanillaBackground(GuiGraphics internal,
								  BackgroundPadding padding,
								  int backgroundTopColor, int backgroundBottomColor, int borderTopColor, int borderBottomColor)
		{
			super(internal, padding);
			this.backgroundTopColor = backgroundTopColor;
			this.backgroundBottomColor = backgroundBottomColor;
			this.borderTopColor = borderTopColor;
			this.borderBottomColor = borderBottomColor;
		}
		
		private VanillaBackground(GuiGraphics internal,
								  BackgroundPadding padding)
		{
			this(internal, padding, 0xF0100010, 0xF0100010, 0x505000FF, 0x5028007F);
		}
		
		@Override
		public GuiGraphics internal()
		{
			return internal;
		}
		
		@Override
		public void render(int x, int y, int width, int height)
		{
			TooltipRenderUtil.renderTooltipBackground(
					internal,
					x + 3 - padding.left(), y + 3 - padding.top(),
					width - 6 + padding.left() + padding.right(), height - 6 + padding.top() + padding.bottom(),
					400,
					backgroundTopColor, backgroundBottomColor, borderTopColor, borderBottomColor
			);
		}
	}
	
	final class NineSpliceBackground extends Background
	{
		private final ResourceLocation asset;
		private final int              textureWidth;
		private final int              textureHeight;
		private final int              border;
		
		private NineSpliceBackground(GuiGraphics internal,
									 BackgroundPadding padding,
									 ResourceLocation asset,
									 int textureWidth, int textureHeight,
									 int border)
		{
			super(internal, padding);
			this.asset = asset;
			this.textureWidth = textureWidth;
			this.textureHeight = textureHeight;
			this.border = border;
		}
		
		@Override
		public GuiGraphics internal()
		{
			return internal;
		}
		
		@Override
		public void render(int x, int y, int width, int height)
		{
			var graphics = new TesseractGuiGraphics(internal);
			
			graphics.setTexture(asset);
			graphics.nineSlice(
					x - padding.left(), y - padding.top(),
					width + padding.left() + padding.right(),
					height + padding.top() + padding.bottom(),
					textureWidth, textureHeight, border
			);
		}
	}
}
