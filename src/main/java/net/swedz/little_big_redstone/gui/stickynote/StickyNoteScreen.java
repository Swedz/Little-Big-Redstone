package net.swedz.little_big_redstone.gui.stickynote;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.client.StickyNoteViewRenderer;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.little_big_redstone.gui.stickynote.reference.StickyNoteReference;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

public abstract class StickyNoteScreen extends Screen
{
	protected final StickyNoteReference reference;
	
	protected final DyeColor color, textColor;
	protected final String initialText;
	
	protected int uiWidth, uiHeight;
	protected int leftPos, topPos;
	
	protected final int contentLeftPos, contentTopPos;
	protected final int maxContentWidth, maxContentHeight;
	
	protected StickyNoteScreen(StickyNoteReference reference)
	{
		super(GameNarrator.NO_TITLE);
		
		this.reference = reference;
		
		this.color = reference.color();
		this.textColor = reference.textColor();
		this.initialText = reference.text();
		
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
	
	@Override
	public void renderBackground(GuiGraphics vanilla, int mouseX, int mouseY, float partialTick)
	{
		super.renderBackground(vanilla, mouseX, mouseY, partialTick);
		
		var graphics = new TesseractGuiGraphics(vanilla);
		
		graphics.pose().pushPose();
		graphics.pose().translate(leftPos, topPos, 0);
		
		StickyNoteViewRenderer.renderBackground(graphics, new StickyNoteView(color, textColor, Component.empty()), true);
		
		graphics.pose().popPose();
	}
}
