package net.swedz.redstone_circuitry.helper;

public final class LogicHelper
{
	public static int toInt(boolean value)
	{
		return value ? 1 : 0;
	}
	
	public static boolean toBoolean(int value)
	{
		return value > 0;
	}
}
