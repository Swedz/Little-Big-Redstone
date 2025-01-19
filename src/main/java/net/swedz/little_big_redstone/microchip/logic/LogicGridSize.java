package net.swedz.little_big_redstone.microchip.logic;

import net.minecraft.client.gui.navigation.ScreenRectangle;

public record LogicGridSize(int width, int height)
{
	public boolean isSingle()
	{
		return width == 1 && height == 1;
	}
	
	public int widthPixels()
	{
		return width * 16;
	}
	
	public int heightPixels()
	{
		return height * 16;
	}
	
	public int centerX()
	{
		return this.widthPixels() / 2;
	}
	
	public int centerY()
	{
		return this.heightPixels() / 2;
	}
	
	public int topLeftCornerX(int x)
	{
		return x - this.centerX();
	}
	
	public int topLeftCornerY(int y)
	{
		return y - this.centerY();
	}
	
	public int portTopLeftCornerX(int x, boolean input, int index, int totalPorts)
	{
		int halfX = this.centerX();
		return x + halfX + (input ? -halfX - 16 : halfX);
	}
	
	public int portTopLeftCornerY(int y, boolean input, int index, int totalPorts)
	{
		int halfY = this.centerY();
		int portPadding = this.heightPixels() / totalPorts;
		return y - 8 + (portPadding * index) + (portPadding / 2) + (totalPorts % 2 == 0 || totalPorts == 1 ? 0 : 1);
	}
	
	public ScreenRectangle portBounds(int x, int y, boolean input, int index, int totalPorts)
	{
		int portX = this.portTopLeftCornerX(x, input, index, totalPorts);
		int portY = this.portTopLeftCornerY(y, input, index, totalPorts);
		if(input)
		{
			portX += 13;
		}
		portY += 5;
		return new ScreenRectangle(portX, portY, 3, 4 + 2);
	}
}
