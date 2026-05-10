package net.swedz.little_big_redstone.gui.stickynote.reference;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;
import net.swedz.little_big_redstone.microchip.object.note.StickyNoteEntry;
import net.swedz.little_big_redstone.network.packet.StickyNotePacket;

public final class MicrochipStickyNoteReference implements StickyNoteReference
{
	private final int slot;
	
	private final DyeColor color, textColor;
	
	private final String text;
	
	public MicrochipStickyNoteReference(StickyNoteEntry entry)
	{
		this(entry.slot(), entry.noteColor(), entry.textColor(), entry.note().text());
	}
	
	private MicrochipStickyNoteReference(int slot, DyeColor color, DyeColor textColor, String text)
	{
		this.slot = slot;
		this.color = color;
		this.textColor = textColor;
		this.text = text;
	}
	
	@Override
	public DyeColor color()
	{
		return color;
	}
	
	@Override
	public DyeColor textColor()
	{
		return textColor;
	}
	
	@Override
	public String text()
	{
		return text;
	}
	
	@Override
	public StickyNoteReference withText(String text)
	{
		return new MicrochipStickyNoteReference(slot, color, textColor, text);
	}
	
	private void save(Player player)
	{
		if(player.containerMenu instanceof MicrochipMenu menu &&
		   menu.stillValid(player))
		{
			var entry = menu.microchip().stickyNotes().get(slot);
			if(entry != null)
			{
				entry.setNote(new StickyNote(text));
				menu.microchip().markDirty(false);
			}
		}
	}
	
	@Override
	public void saveClient(Level level, Player player)
	{
		this.save(player);
		new StickyNotePacket(StickyNotePacket.ReferenceType.MICROCHIP, slot, StickyNotePacket.Action.DONE_EDIT, text).sendToServer();
	}
	
	@Override
	public void saveServer(Level level, Player player)
	{
		this.save(player);
	}
	
	@Override
	public boolean isStillValid(Level level, Player player)
	{
		return player.containerMenu instanceof MicrochipMenu menu &&
			   menu.stillValid(player) &&
			   menu.microchip().stickyNotes().get(slot) != null;
	}
}
