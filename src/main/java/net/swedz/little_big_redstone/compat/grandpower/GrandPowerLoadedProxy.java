package net.swedz.little_big_redstone.compat.grandpower;

import dev.technici4n.grandpower.api.ILongEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint;
import net.swedz.tesseract.neoforge.proxy.ProxyEnvironment;

@ProxyEntrypoint(environment = ProxyEnvironment.MOD, modid = "grandpower")
public class GrandPowerLoadedProxy extends GrandPowerProxy
{
	@Override
	public long getEnergyStored(IEnergyStorage handler)
	{
		if(handler instanceof ILongEnergyStorage longHandler)
		{
			return longHandler.getAmount();
		}
		return super.getEnergyStored(handler);
	}
	
	@Override
	public long getMaxEnergyStored(IEnergyStorage handler)
	{
		if(handler instanceof ILongEnergyStorage longHandler)
		{
			return longHandler.getCapacity();
		}
		return super.getMaxEnergyStored(handler);
	}
}
