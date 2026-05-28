package net.swedz.little_big_redstone.item.stickynote.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.swedz.little_big_redstone.LBRClient;
import net.swedz.little_big_redstone.client.StickyNoteViewRenderer;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;

public final class StickyNoteClientTooltip implements ClientTooltipComponent
{
	private final StickyNoteView note;
	
	public StickyNoteClientTooltip(StickyNoteView note)
	{
		this.note = note;
	}
	
	private int getSize()
	{
		return (int) (LBRClient.config().stickyNoteTooltipViewScale() * 180);
	}
	
	@Override
	public int getHeight(Font font)
	{
		return this.getSize() + 4;
	}
	
	@Override
	public int getWidth(Font font)
	{
		return this.getSize();
	}
	
	@Override
	public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics)
	{
		float scale = (float) LBRClient.config().stickyNoteTooltipViewScale();
		
		graphics.pose().pushMatrix();
		graphics.pose().translate(x, y);
		graphics.pose().scale(scale, scale);
		
		StickyNoteViewRenderer.extractBackground(graphics, note);
		StickyNoteViewRenderer.extractText(graphics, note);
		
		graphics.pose().popMatrix();
	}
}
