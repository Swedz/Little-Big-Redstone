package net.swedz.little_big_redstone.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.client.hud.FloppyDiskConsumeItemsGuiOverlay;
import net.swedz.little_big_redstone.gui.floppydisk.FloppyDiskScreen;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;
import net.swedz.little_big_redstone.gui.stickynote.edit.StickyNoteEditScreen;
import net.swedz.little_big_redstone.gui.stickynote.view.StickyNoteViewScreen;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;
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
			screen.getMenu().microchip().loadFrom(microchip);
			screen.handleUpdate();
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
	
	@Override
	public void openStickyNote(int entityId, DyeColor color, DyeColor textColor, String text, boolean edit)
	{
		var minecraft = Minecraft.getInstance();
		if(edit)
		{
			minecraft.setScreen(new StickyNoteEditScreen(entityId, color, textColor, text, false));
		}
		else
		{
			minecraft.setScreen(new StickyNoteViewScreen(entityId, color, textColor, text));
		}
	}
	
	@Override
	public void floppyDiskGuiOverlayUpdate(boolean force)
	{
		FloppyDiskConsumeItemsGuiOverlay.update(force);
	}
	
	private Microchip.Immutable watchedMicrochip;
	
	@Override
	public void updateWatchedMicrochip(Microchip microchip)
	{
		watchedMicrochip = microchip == null ? null : microchip.immutable();
		FloppyDiskConsumeItemsGuiOverlay.update(true);
	}
	
	@Override
	public Microchip.Immutable getWatchedMicrochip()
	{
		return watchedMicrochip;
	}
	
	@Override
	public void openFloppyDisk(InteractionHand hand)
	{
		Minecraft.getInstance().setScreen(new FloppyDiskScreen(hand));
	}
}
