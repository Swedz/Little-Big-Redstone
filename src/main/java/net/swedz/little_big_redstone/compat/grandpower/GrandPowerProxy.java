package net.swedz.little_big_redstone.compat.grandpower;

import net.neoforged.neoforge.energy.IEnergyStorage;
import net.swedz.tesseract.neoforge.proxy.Proxy;
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint;

@ProxyEntrypoint
public class GrandPowerProxy implements Proxy
{
	public long getEnergyStored(IEnergyStorage handler)
	{
		return handler.getEnergyStored();
	}
	
	public long getMaxEnergyStored(IEnergyStorage handler)
	{
		return handler.getMaxEnergyStored();
	}
}
