package net.swedz.little_big_redstone.helper;

import net.minecraft.core.Direction;

// TODO move to Tesseract
public final class DirectionHelper
{
	public static Direction relativeUp(Direction side, Direction facing)
	{
		return side == Direction.UP ? facing.getOpposite() : (side == Direction.DOWN ? facing : Direction.UP);
	}
	
	public static Direction relativeDown(Direction side, Direction facing)
	{
		return relativeUp(side, facing).getOpposite();
	}
	
	public static Direction relativeLeft(Direction side, Direction facing)
	{
		return (side.getAxis().isVertical() ? facing : side).getClockWise();
	}
	
	public static Direction relativeRight(Direction side, Direction facing)
	{
		return relativeLeft(side, facing).getOpposite();
	}
}
