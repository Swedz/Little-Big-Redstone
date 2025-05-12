package net.swedz.little_big_redstone.gui.stickynote;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;

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
	
	@Override
	public void renderBackground(GuiGraphics vanilla, int mouseX, int mouseY, float partialTick)
	{
		super.renderBackground(vanilla, mouseX, mouseY, partialTick);
		
		var graphics = new TesseractGuiGraphics(vanilla);
		
		graphics.pose().pushPose();
		graphics.pose().translate(leftPos, topPos, 0);
		
		graphics.setColor(LBRColors.stickyNoteBackground(color));
		graphics.setTexture(LBR.id("textures/gui/sticky_note.png"));
		graphics.blit(0, 0, 0, 0, 256, 256);
		
		graphics.setColor(LBRColors.stickyNotePin(color));
		graphics.setTexture(LBR.id("textures/gui/sticky_note_pin.png"));
		graphics.blit(0, 0, 0, 0, 256, 256);
		
		graphics.resetColor();
		
		graphics.pose().popPose();
	}
}
