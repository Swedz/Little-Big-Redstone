package net.swedz.little_big_redstone.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

public final class StickyNoteViewRenderer
{
	public static void renderBackground(TesseractGuiGraphics graphics, DyeColor color, boolean pin, float alpha)
	{
		graphics.pose().pushPose();
		graphics.setColor(1, 1, 1, alpha);
		
		graphics.setTexture(LBR.id("textures/gui/sticky_note/background_%s.png".formatted(color.getName())));
		graphics.nineSlice(0, 0, 180, 180, 64, 64, 21);
		
		if(pin)
		{
			graphics.setTexture(LBR.id("textures/gui/sticky_note/pin_%s.png".formatted(color.getName())));
			graphics.blit((180 / 2) - 9, 4, 0, 0, 32, 32, 32, 32);
		}
		
		graphics.resetColor();
		graphics.pose().popPose();
	}
	
	public static void renderBackground(TesseractGuiGraphics graphics, StickyNoteView note, float alpha)
	{
		renderBackground(graphics, note.color(), true, alpha);
	}
	
	public static void renderBackground(TesseractGuiGraphics graphics, StickyNoteView note)
	{
		renderBackground(graphics, note, 1);
	}
	
	public static void renderText(TesseractGuiGraphics graphics, DyeColor textColor, Component text, float alpha)
	{
		var font = Minecraft.getInstance().font;
		
		graphics.pose().pushPose();
		graphics.pose().translate(5, 27, 0);
		
		graphics.setColor(LBRColors.stickyNoteText(textColor, alpha));
		graphics.setStringDropShadow(false);
		int index = 0;
		for(var line : font.split(text, 170))
		{
			int y = index * font.lineHeight;
			graphics.drawString(line, 0, y);
			index++;
		}
		graphics.setStringDropShadow(true);
		graphics.resetColor();
		
		graphics.pose().popPose();
	}
	
	public static void renderText(TesseractGuiGraphics graphics, StickyNoteView note, float alpha)
	{
		renderText(graphics, note.textColor(), note.text(), alpha);
	}
	
	public static void renderText(TesseractGuiGraphics graphics, StickyNoteView note)
	{
		renderText(graphics, note, 1);
	}
}
