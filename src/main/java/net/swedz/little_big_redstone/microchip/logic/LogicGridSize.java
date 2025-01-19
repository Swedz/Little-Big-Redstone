package net.swedz.little_big_redstone.microchip.logic;

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
	
	public int portTopLeftCornerX(int x, boolean input, int index, int maxPorts)
	{
		int halfX = this.centerX();
		return x + halfX + (input ? -halfX - 16 : halfX);
	}
	
	public int portTopLeftCornerY(int y, boolean input, int index, int maxPorts)
	{
		int halfY = this.centerY();
		int portPadding = this.heightPixels() / maxPorts;
		return y - 8 + (portPadding * index) + (portPadding / 2) + (maxPorts % 2 == 0 || maxPorts == 1 ? 0 : 1);
	}
}
