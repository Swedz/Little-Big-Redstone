package net.swedz.little_big_redstone.gui.stickynote;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;

public final class StickyNoteViewScreen extends Screen
{
	private final int entityId;
	
	private final Component text;
	
	public StickyNoteViewScreen(int entityId, String text)
	{
		super(GameNarrator.NO_TITLE);
		this.entityId = entityId;
		this.text = StickyNote.parse(text);
	}
	
	@Override
	protected void init()
	{
		// TODO
	}
}
