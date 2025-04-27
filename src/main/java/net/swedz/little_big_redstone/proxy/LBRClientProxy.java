package net.swedz.little_big_redstone.proxy;

import net.minecraft.client.Minecraft;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;
import net.swedz.little_big_redstone.microchip.LogicEntry;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint;
import net.swedz.tesseract.neoforge.proxy.ProxyEnvironment;

import java.util.List;

@ProxyEntrypoint(environment = ProxyEnvironment.CLIENT)
public class LBRClientProxy extends LBRProxy
{
	@Override
	public void handleUpdateMicrochip(int containerId, Microchip microchip)
	{
		if(Minecraft.getInstance().screen instanceof MicrochipScreen screen &&
		   screen.getMenu().containerId == containerId)
		{
			screen.handleUpdate();
			screen.getMenu().microchip().loadFrom(microchip);
		}
		else
		{
			LBR.LOGGER.warn("Received UpdateMicrochipPacket while not in a microchip screen, discarding");
		}
	}
	
	@Override
	public void handleUpdateComponentsMicrochip(int containerId, List<LogicEntry> entries)
	{
		if(Minecraft.getInstance().screen instanceof MicrochipScreen screen &&
		   screen.getMenu().containerId == containerId)
		{
			screen.handleUpdate();
			for(var entry : entries)
			{
				var existingEntry = screen.getMenu().microchip().components().get(entry.slot());
				if(existingEntry == null)
				{
					continue;
				}
				existingEntry.component().loadFrom(entry.component());
			}
		}
		else
		{
			LBR.LOGGER.warn("Received UpdateComponentsMicrochipPacket while not in a microchip screen, discarding");
		}
	}
}
