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
}
