package net.swedz.little_big_redstone.microchip.logic;

public final class LogicContext
{
	private boolean changed;
	
	public boolean changed()
	{
		return changed;
	}
	
	public void flagChanged()
	{
		changed = true;
	}
}
