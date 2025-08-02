package net.swedz.little_big_redstone.gui.logicconfig.button.iconcycle;

public enum CheckboxState implements IconCycleLogicConfigButtonIcon
{
	YES(90, 0),
	NO(108, 0);
	
	private final int u, v;
	
	CheckboxState(int u, int v)
	{
		this.u = u;
		this.v = v;
	}
	
	@Override
	public int u()
	{
		return u;
	}
	
	@Override
	public int v()
	{
		return v;
	}
}
