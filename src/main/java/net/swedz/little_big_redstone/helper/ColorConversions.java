package net.swedz.little_big_redstone.helper;

import net.swedz.tesseract.neoforge.api.Assert;

// TODO move to Tesseract
public final class ColorConversions
{
	public static int asInt(float part)
	{
		Assert.that(part >= 0 && part <= 1);
		return (int) (part * 255);
	}
	
	public static float asFloat(int part)
	{
		Assert.that(part >= 0 && part <= 255);
		return part / 255f;
	}
	
	public static int asARGB(int rgb)
	{
		return (0xFF << 24) | (rgb & 0x00FFFFFF);
	}
	
	public static int asARGB(int rgb, int alpha)
	{
		return ((alpha & 0xFF) << 24) | (rgb & 0x00FFFFFF);
	}
	
	public static int asRGB(int argb)
	{
		return argb & 0x00FFFFFF;
	}
	
	public static int alphaInt(int argb)
	{
		return (argb >> 24) & 0xFF;
	}
	
	public static int redInt(int argb)
	{
		return (argb >> 16) & 0xFF;
	}
	
	public static int greenInt(int argb)
	{
		return (argb >> 8) & 0xFF;
	}
	
	public static int blueInt(int argb)
	{
		return argb & 0xFF;
	}
	
	public static float alphaFloat(int argb)
	{
		return asFloat(alphaInt(argb));
	}
	
	public static float redFloat(int argb)
	{
		return asFloat(redInt(argb));
	}
	
	public static float greenFloat(int argb)
	{
		return asFloat(greenInt(argb));
	}
	
	public static float blueFloat(int argb)
	{
		return asFloat(blueInt(argb));
	}
}
