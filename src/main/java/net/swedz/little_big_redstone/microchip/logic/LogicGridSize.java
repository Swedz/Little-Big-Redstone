package net.swedz.little_big_redstone.microchip.logic;

import net.swedz.little_big_redstone.api.Bounds;

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
	
	public Bounds toBounds(int x, int y)
	{
		return new Bounds(x, y, this.widthPixels(), this.heightPixels());
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
		int remainingPortPadding = this.heightPixels() - (portPadding * totalPorts);
		return y - 8 + (portPadding * index) + (portPadding / 2) + (int) Math.ceil(remainingPortPadding / 2f);
	}
	
	public Bounds portBounds(int x, int y, boolean input, int index, int totalPorts)
	{
		int portX = this.portTopLeftCornerX(x, input, index, totalPorts);
		int portY = this.portTopLeftCornerY(y, input, index, totalPorts);
		if(input)
		{
			portX += 10;
		}
		portY += 5;
		return new Bounds(portX, portY, 6, 6);
	}
}
