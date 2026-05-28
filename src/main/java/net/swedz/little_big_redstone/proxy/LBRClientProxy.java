package net.swedz.little_big_redstone.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.client.hud.FloppyDiskConsumeItemsGuiOverlay;
import net.swedz.little_big_redstone.client.hud.StickyNoteViewContentsGuiOverlay;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.little_big_redstone.gui.floppydisk.FloppyDiskScreen;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;
import net.swedz.little_big_redstone.gui.noteboard.NoteBoardScreen;
import net.swedz.little_big_redstone.gui.noteboard.contents.NoteBoardContents;
import net.swedz.little_big_redstone.gui.stickynote.edit.StickyNoteEditScreen;
import net.swedz.little_big_redstone.gui.stickynote.reference.StickyNoteReference;
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
	public void handleUpdateMicrochip(int containerId, Microchip microchip, boolean rerouteWires)
	{
		if(Minecraft.getInstance().player.containerMenu instanceof MicrochipMenu menu &&
		   menu.containerId == containerId)
		{
			menu.microchip().loadFrom(microchip);
			if(Minecraft.getInstance().screen instanceof MicrochipScreen screen)
			{
				screen.handleUpdate(rerouteWires);
			}
		}
		else
		{
			LBR.LOGGER.warn("Received UpdateMicrochipPacket while not in a microchip screen, discarding");
		}
	}
	
	@Override
	public void handleUpdateComponentsMicrochip(int containerId, List<LogicEntry> entries)
	{
		if(Minecraft.getInstance().player.containerMenu instanceof MicrochipMenu menu &&
		   menu.containerId == containerId)
		{
			for(var entry : entries)
			{
				var existingEntry = menu.microchip().components().get(entry.slot());
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
	public void openStickyNote(StickyNoteReference reference, boolean edit)
	{
		var minecraft = Minecraft.getInstance();
		if(edit)
		{
			minecraft.setScreen(new StickyNoteEditScreen(reference, false));
		}
		else
		{
			minecraft.setScreen(new StickyNoteViewScreen(reference));
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
	
	private StickyNoteView watchedStickyNote;
	
	@Override
	public void updateWatchedStickyNote(StickyNoteView stickyNote)
	{
		watchedStickyNote = stickyNote;
		StickyNoteViewContentsGuiOverlay.update(watchedStickyNote);
	}
	
	@Override
	public StickyNoteView getWatchedStickyNote()
	{
		return watchedStickyNote;
	}
	
	@Override
	public void openFloppyDisk(InteractionHand hand)
	{
		Minecraft.getInstance().setScreen(new FloppyDiskScreen(hand));
	}
	
	@Override
	public void setPickedItem(ItemStack stack)
	{
		var minecraft = Minecraft.getInstance();
		var player = minecraft.player;
		var inventory = player.getInventory();
		
		inventory.setSelectedItem(stack);
		minecraft.gameMode.handleCreativeModeItemAdd(player.getItemInHand(InteractionHand.MAIN_HAND), 36 + inventory.getSelectedSlot());
	}
	
	@Override
	public void updateNoteBoardContentsInScreen(NoteBoardContents contents)
	{
		if(Minecraft.getInstance().screen instanceof NoteBoardScreen screen)
		{
			screen.setContents(contents);
		}
	}
}
