package net.swedz.little_big_redstone.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.tesseract.neoforge.helper.gui.ExtraGuiGraphics;

public final class StickyNoteViewRenderer
{
	public static void extractBackground(GuiGraphicsExtractor graphics, DyeColor color, boolean pin, float alpha, int packedLight)
	{
		int argb = (Math.round(alpha * 255f) << 24) | 0x00FFFFFF;
		
		var backgroundTexture = LBR.id("textures/gui/sticky_note/background_%s.png".formatted(color.getName()));
		ExtraGuiGraphics.nineSlice(graphics, backgroundTexture, argb, packedLight, 0, 0, 180, 180, 64, 64, 21);
		
		if(pin)
		{
			var pinTexture = LBR.id("textures/gui/sticky_note/pin_%s.png".formatted(color.getName()));
			graphics.blitLight(RenderPipelines.GUI, pinTexture, (180 / 2) - 9, 4, 0, 0, 32, 32, 32, 32, 32, 32, argb, packedLight);
		}
	}
	
	public static void extractBackground(GuiGraphicsExtractor graphics, StickyNoteView note, float alpha, int packedLight)
	{
		extractBackground(graphics, note.color(), true, alpha, packedLight);
	}
	
	public static void extractBackground(GuiGraphicsExtractor graphics, StickyNoteView note, float alpha)
	{
		extractBackground(graphics, note, alpha, 0xFFFFFFFF);
	}
	
	public static void extractBackground(GuiGraphicsExtractor graphics, StickyNoteView note)
	{
		extractBackground(graphics, note, 1, 0xFFFFFFFF);
	}
	
	// TODO packedLight
	public static void extractText(GuiGraphicsExtractor graphics, DyeColor textColor, Component text, float alpha)
	{
		var font = Minecraft.getInstance().font;
		
		graphics.pose().pushMatrix();
		graphics.pose().translate(5, 27);
		
		int argb = LBRColors.stickyNoteText(textColor, alpha);
		int index = 0;
		for(var line : font.split(text, 170))
		{
			int y = index * font.lineHeight;
			graphics.text(Minecraft.getInstance().font, line, 0, y, argb, false);
			index++;
		}
		
		graphics.pose().popMatrix();
	}
	
	public static void extractText(GuiGraphicsExtractor graphics, StickyNoteView note, float alpha)
	{
		extractText(graphics, note.textColor(), note.text(), alpha);
	}
	
	public static void extractText(GuiGraphicsExtractor graphics, StickyNoteView note)
	{
		extractText(graphics, note, 1);
	}
}
