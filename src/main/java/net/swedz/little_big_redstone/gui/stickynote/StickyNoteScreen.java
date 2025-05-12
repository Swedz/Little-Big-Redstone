package net.swedz.little_big_redstone.gui.stickynote;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.DyeColor;

public abstract class StickyNoteScreen extends Screen
{
	protected final int      entityId;
	protected final DyeColor color;
	protected final String   initialText;
	
	protected int uiWidth, uiHeight;
	protected int leftPos, topPos;
	
	protected int contentLeftPos, contentTopPos;
	protected int maxContentWidth, maxContentHeight;
	
	protected StickyNoteScreen(int entityId, DyeColor color, String text)
	{
		super(GameNarrator.NO_TITLE);
		
		this.entityId = entityId;
		this.color = color;
		this.initialText = text;
		
		uiWidth = 180;
		uiHeight = 214;
		
		contentLeftPos = 5;
		contentTopPos = 27;
		maxContentWidth = 170;
		maxContentHeight = 126;
	}
	
	@Override
	protected void init()
	{
		leftPos = width / 2 - uiWidth / 2;
		topPos = height / 2 - uiHeight / 2;
	}
}
