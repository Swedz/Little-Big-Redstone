package net.swedz.little_big_redstone.proxy;

import net.swedz.little_big_redstone.microchip.LogicEntry;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.tesseract.neoforge.proxy.Proxy;
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint;

import java.util.List;

@ProxyEntrypoint
public class LBRProxy implements Proxy
{
	public void handleUpdateMicrochip(int containerId, Microchip microchip)
	{
	}
	
	public void handleUpdateComponentsMicrochip(int containerId, List<LogicEntry> entries)
	{
	}
}
