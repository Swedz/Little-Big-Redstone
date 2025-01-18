package net.swedz.little_big_redstone.microchip.logic;

public final class LogicContext
{
	private boolean dirty;
	
	public boolean isDirty()
	{
		return dirty;
	}
	
	public void markDirty()
	{
		dirty = true;
	}
}
