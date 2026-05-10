package net.swedz.little_big_redstone.microchip.object.logic;

import net.swedz.tesseract.neoforge.api.Bounds;

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
	
	public int portX(int x, boolean input, int index, int totalPorts)
	{
		return input ? x : (x + this.widthPixels());
	}
	
	public int portY(int y, boolean input, int index, int totalPorts)
	{
		return this.portTopLeftCornerY(y, input, index, totalPorts) + 8;
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
	
	public int wireOutStartX(int x)
	{
		return x + this.widthPixels() + 3;
	}
	
	public int wireOutStartY(int y, int portIndex, int totalPorts)
	{
		return this.portTopLeftCornerY(y, false, portIndex, totalPorts) + 8 - 1;
	}
	
	public int wireInEndX(int x)
	{
		return x - 5;
	}
	
	public int wireInEndY(int y, int portIndex, int totalPorts)
	{
		return this.portTopLeftCornerY(y, true, portIndex, totalPorts) + 8 - 1;
	}
}
