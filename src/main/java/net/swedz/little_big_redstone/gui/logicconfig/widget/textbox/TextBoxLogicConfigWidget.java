package net.swedz.little_big_redstone.gui.logicconfig.widget.textbox;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.swedz.little_big_redstone.gui.logicconfig.widget.LogicConfigButtonHelper;
import net.swedz.little_big_redstone.gui.logicconfig.widget.TickableLogicConfigWidget;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

public class TextBoxLogicConfigWidget extends EditBox implements LogicConfigButtonHelper, TickableLogicConfigWidget
{
	private final int color;
	
	private int tick;
	
	public TextBoxLogicConfigWidget(Font font, int x, int y, int width, int height, int color, Component message)
	{
		super(font, x, y, width, height, message);
		this.color = color;
	}
	
	@Override
	public void tick()
	{
		tick++;
	}
	
	private void renderText(TesseractGuiGraphics graphics)
	{
		var displayedText = font.plainSubstrByWidth(value.substring(displayPos), this.getInnerWidth());
		
		int startX = this.getX() + 4;
		int textY = this.getY() + (height - 8) / 2;
		
		graphics.setColor(color);
		graphics.setStringDropShadow(false);
		
		graphics.drawString(displayedText, startX, textY);
		
		boolean isHighlighted = highlightPos != cursorPos;
		if(isHighlighted)
		{
			int highlightIndex = Mth.clamp(highlightPos - displayPos, 0, displayedText.length());
			int cursorIndex = Mth.clamp(cursorPos - displayPos, 0, displayedText.length());
			
			int highlightStartX = startX + font.width(displayedText.substring(0, highlightIndex));
			int highlightEndX = startX + font.width(displayedText.substring(0, cursorIndex));
			
			graphics.fill(highlightStartX, textY, highlightEndX, textY + font.lineHeight - 1);
			
			graphics.setColor(0x00000000);
			graphics.drawString(displayedText.substring(Math.min(highlightIndex, cursorIndex), Math.max(highlightIndex, cursorIndex)), Math.min(highlightStartX, highlightEndX), textY);
			graphics.revertColor();
		}
		
		if(this.isFocused() && (tick / 6) % 2 == 0)
		{
			int cursorIndex = cursorPos - displayPos;
			if(cursorIndex >= 0 && cursorIndex <= displayedText.length())
			{
				int cursorX = font.width(displayedText.substring(0, cursorIndex));
				if(cursorIndex == displayedText.length())
				{
					graphics.drawString("_", startX + cursorX, textY);
				}
				else
				{
					int startY = this.getY() + 4;
					graphics.fill(startX + cursorX - 1, startY, startX + cursorX, startY + font.lineHeight + 1);
				}
			}
		}
		
		graphics.resetColor();
	}
	
	@Override
	public void renderWidget(GuiGraphics internal, int mouseX, int mouseY, float partialTick)
	{
		if(!this.isVisible())
		{
			return;
		}
		
		var graphics = new TesseractGuiGraphics(internal);
		
		this.renderBorder(graphics, this.getX(), this.getY(), width, height, color);
		
		this.renderText(graphics);
	}
}
