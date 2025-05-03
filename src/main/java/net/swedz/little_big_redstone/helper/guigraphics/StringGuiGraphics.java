package net.swedz.little_big_redstone.helper.guigraphics;

import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public interface StringGuiGraphics extends TextGuiGraphics
{
	default int drawString(String text, int x, int y)
	{
		return this.drawString(text, x, y, true);
	}
	
	default int drawString(String text, int x, int y, boolean dropShadow)
	{
		return this.drawString(text, (float) x, (float) y, dropShadow);
	}
	
	default int drawString(FormattedCharSequence text, int x, int y)
	{
		return this.drawString(text, x, y, true);
	}
	
	default int drawString(FormattedCharSequence text, int x, int y, boolean dropShadow)
	{
		return this.drawString(text, (float) x, (float) y, dropShadow);
	}
	
	default int drawString(Component text, int x, int y)
	{
		return this.drawString(text, x, y, true);
	}
	
	default int drawString(Component text, int x, int y, boolean dropShadow)
	{
		return this.drawString(text.getVisualOrderText(), x, y, dropShadow);
	}
	
	int drawString(String text, float x, float y, boolean dropShadow);
	
	int drawString(FormattedCharSequence text, float x, float y, boolean dropShadow);
}
