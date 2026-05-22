package net.swedz.little_big_redstone.gui.stickynote.reference;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.swedz.little_big_redstone.LBRAttachments;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.gui.noteboard.NoteBoardMenu;
import net.swedz.little_big_redstone.gui.noteboard.contents.NoteBoardStickyNote;
import net.swedz.little_big_redstone.network.packet.StickyNotePacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.proxy.Proxies;

public final class NoteBoardStickyNoteReference implements StickyNoteReference
{
	private final int index;
	
	private final DyeColor color, textColor;
	
	private final String text;
	
	private final boolean editable;
	
	private NoteBoardStickyNoteReference(int index, DyeColor color, DyeColor textColor, String text, boolean editable)
	{
		this.index = index;
		this.color = color;
		this.textColor = textColor;
		this.text = text;
		this.editable = editable;
	}
	
	public static NoteBoardStickyNoteReference from(int index, NoteBoardStickyNote note)
	{
		var noteView = note.asView();
		var text = note.stack().get(LBRComponents.STICKY_NOTE).text();
		return new NoteBoardStickyNoteReference(index, noteView.color(), noteView.textColor(), text, note.isEditable());
	}
	
	public int index()
	{
		return index;
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
	public boolean canEdit()
	{
		return editable;
	}
	
	@Override
	public StickyNoteReference withText(String text)
	{
		return new NoteBoardStickyNoteReference(index, color, textColor, text, editable);
	}
	
	private void save(Player player)
	{
		if(player.containerMenu instanceof NoteBoardMenu menu &&
		   menu.stillValid(player))
		{
			var noteBoard = player.getData(LBRAttachments.NOTE_BOARD);
			if(noteBoard.has(index))
			{
				var note = noteBoard.get(index);
				if(note.isEditable())
				{
					noteBoard = noteBoard.update(index, note.withText(text));
					if(player.level().isClientSide())
					{
						Proxies.get(LBRProxy.class).updateNoteBoardContentsInScreen(noteBoard);
					}
					else
					{
						player.setData(LBRAttachments.NOTE_BOARD, noteBoard);
					}
				}
			}
		}
	}
	
	@Override
	public void saveClient(Level level, Player player)
	{
		this.save(player);
		new StickyNotePacket(StickyNotePacket.ReferenceType.NOTE_BOARD, index, StickyNotePacket.Action.DONE_EDIT, text).sendToServer();
	}
	
	@Override
	public void saveServer(Level level, Player player)
	{
		this.save(player);
	}
	
	@Override
	public boolean isStillValid(Level level, Player player)
	{
		return player.containerMenu instanceof NoteBoardMenu menu &&
			   menu.stillValid(player) &&
			   player.getData(LBRAttachments.NOTE_BOARD).has(index);
	}
}
