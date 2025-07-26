package net.swedz.little_big_redstone.proxy;

import net.minecraft.world.InteractionHand;
import net.swedz.little_big_redstone.gui.stickynote.reference.StickyNoteReference;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;
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
	
	public void openStickyNote(StickyNoteReference reference, boolean edit)
	{
	}
	
	public void floppyDiskGuiOverlayUpdate(boolean force)
	{
	}
	
	public void updateWatchedMicrochip(Microchip microchip)
	{
	}
	
	public Microchip.Immutable getWatchedMicrochip()
	{
		throw new UnsupportedOperationException("getWatchedMicrochip() can only be called on the client");
	}
	
	public void openFloppyDisk(InteractionHand hand)
	{
	}
}
