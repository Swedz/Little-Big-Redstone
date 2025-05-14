package net.swedz.little_big_redstone.helper.guigraphics;

import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public interface StringGuiGraphics extends TextGuiGraphics
{
	boolean isStringDropShadow();
	
	void setStringDropShadow(boolean textDropShadow);
	
	default int drawString(String text, int x, int y)
	{
		return this.drawString(text, (float) x, (float) y);
	}
	
	default int drawString(Component text, int x, int y)
	{
		return this.drawString(text.getVisualOrderText(), x, y);
	}
	
	default int drawString(FormattedCharSequence text, int x, int y)
	{
		return this.drawString(text, (float) x, (float) y);
	}
	
	int drawString(String text, float x, float y);
	
	int drawString(FormattedCharSequence text, float x, float y);
}
