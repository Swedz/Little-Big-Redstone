package net.swedz.little_big_redstone.microchip;

public record LogicSelectedPort(LogicIndex entry, int portIndex)
{
	public boolean addOutputPort(LogicSelectedPort targetPort)
	{
		return entry.addOutputPort(portIndex, targetPort);
	}
}
