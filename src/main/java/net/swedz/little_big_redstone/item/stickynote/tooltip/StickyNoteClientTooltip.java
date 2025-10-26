package net.swedz.little_big_redstone.item.stickynote.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.swedz.little_big_redstone.client.StickyNoteViewRenderer;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

public final class StickyNoteClientTooltip implements ClientTooltipComponent
{
	private final StickyNoteView note;
	
	public StickyNoteClientTooltip(StickyNoteView note)
	{
		this.note = note;
	}
	
	@Override
	public int getHeight()
	{
		return 94;
	}
	
	@Override
	public int getWidth(Font font)
	{
		return 90;
	}
	
	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics vanilla)
	{
		var graphics = new TesseractGuiGraphics(vanilla);
		
		graphics.pose().pushPose();
		graphics.pose().translate(x, y, 0);
		graphics.pose().scale(0.5f, 0.5f, 0.5f);
		
		StickyNoteViewRenderer.renderBackground(graphics, note);
		StickyNoteViewRenderer.renderText(graphics, note);
		
		graphics.pose().popPose();
	}
}
