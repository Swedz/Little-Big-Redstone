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
	public static void renderBackground(TesseractGuiGraphics graphics, DyeColor color, boolean pin, float alpha, boolean gui)
	{
		graphics.pose().pushPose();
		graphics.setColor(1, 1, 1, alpha);
		
		graphics.setTexture(LBR.id("textures/gui/sticky_note/background_%s.png".formatted(color.getName())));
		graphics.nineSlice(0, 0, 180, 180, 64, 64, 21);
		
		if(pin)
		{
			if(!gui)
			{
				graphics.pose().pushPose();
				graphics.pose().translate(0, 0, -0.1f);
			}
			graphics.setTexture(LBR.id("textures/gui/sticky_note/pin_%s.png".formatted(color.getName())));
			graphics.blit((180 / 2) - 9, 4, 0, 0, 32, 32, 32, 32);
			if(!gui)
			{
				graphics.pose().popPose();
			}
		}
		
		graphics.resetColor();
		graphics.pose().popPose();
	}
	
	public static void renderBackground(TesseractGuiGraphics graphics, StickyNoteView note, float alpha, boolean gui)
	{
		renderBackground(graphics, note.color(), true, alpha, gui);
	}
	
	public static void renderBackground(TesseractGuiGraphics graphics, StickyNoteView note, boolean gui)
	{
		renderBackground(graphics, note, 1, gui);
	}
	
	public static void renderText(TesseractGuiGraphics graphics, DyeColor textColor, Component text, float alpha, boolean gui)
	{
		var font = Minecraft.getInstance().font;
		
		graphics.pose().pushPose();
		graphics.pose().translate(5, 27, gui ? 0 : -0.1f);
		
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
	
	public static void renderText(TesseractGuiGraphics graphics, StickyNoteView note, float alpha, boolean gui)
	{
		renderText(graphics, note.textColor(), note.text(), alpha, gui);
	}
	
	public static void renderText(TesseractGuiGraphics graphics, StickyNoteView note, boolean gui)
	{
		renderText(graphics, note, 1, gui);
	}
}
