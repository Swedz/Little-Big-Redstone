package net.swedz.little_big_redstone.helper.guigraphics;

import net.minecraft.client.renderer.RenderType;

public interface FillGuiGraphics extends ColoredGuiGraphics
{
	default void fill(int minX, int minY, int maxX, int maxY)
	{
		this.fill(minX, minY, maxX, maxY, 0);
	}
	
	default void fill(int minX, int minY, int maxX, int maxY, int z)
	{
		this.fill(RenderType.gui(), minX, minY, maxX, maxY, z);
	}
	
	default void fill(RenderType renderType, int minX, int minY, int maxX, int maxY)
	{
		this.fill(renderType, minX, minY, maxX, maxY, 0);
	}
	
	void fill(RenderType renderType, int minX, int minY, int maxX, int maxY, int z);
}
