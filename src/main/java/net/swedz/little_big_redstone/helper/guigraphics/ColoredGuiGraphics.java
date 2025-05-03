package net.swedz.little_big_redstone.helper.guigraphics;

import net.minecraft.util.FastColor;

public interface ColoredGuiGraphics
{
	int[] getColor();
	
	default int getColorARGB()
	{
		int[] color = this.getColor();
		return FastColor.ARGB32.color(color[3], color[0], color[1], color[2]);
	}
	
	default void setColor(int argb)
	{
		this.setColor(
				FastColor.ARGB32.red(argb),
				FastColor.ARGB32.green(argb),
				FastColor.ARGB32.blue(argb),
				FastColor.ARGB32.alpha(argb)
		);
	}
	
	default void setColor(int[] rgba)
	{
		this.setColor(rgba[0], rgba[1], rgba[2], rgba[3]);
	}
	
	default void setColor(float red, float green, float blue, float alpha)
	{
		this.setColor((int) (red * 255), (int) (green * 255), (int) (blue * 255), (int) (alpha * 255));
	}
	
	void setColor(int red, int green, int blue, int alpha);
	
	default void resetColor()
	{
		this.setColor(255, 255, 255, 255);
	}
}
