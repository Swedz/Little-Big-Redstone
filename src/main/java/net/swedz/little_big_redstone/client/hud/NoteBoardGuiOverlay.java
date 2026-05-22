package net.swedz.little_big_redstone.client.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRAttachments;
import net.swedz.little_big_redstone.client.StickyNoteViewRenderer;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.little_big_redstone.gui.noteboard.contents.NoteBoardContents;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

@EventBusSubscriber(modid = LBR.ID, value = Dist.CLIENT)
public final class NoteBoardGuiOverlay
{
	private static NoteBoardContents CONTENTS = NoteBoardContents.EMPTY;
	
	public static void render(GuiGraphics internal, DeltaTracker delta)
	{
		if(Minecraft.getInstance().options.hideGui)
		{
			return;
		}
		
		var graphics = new TesseractGuiGraphics(internal);
		
		for(var note : CONTENTS)
		{
			renderNote(graphics, note.x(graphics.guiWidth()), note.y(graphics.guiHeight()), note.size(), note.asView());
		}
	}
	
	private static void renderNote(TesseractGuiGraphics graphics, int x, int y, int size, StickyNoteView view)
	{
		graphics.pose().pushPose();
		graphics.pose().translate(x, y, 0);
		float scale = size / 180f;
		graphics.pose().scale(scale, scale, 1);
		
		StickyNoteViewRenderer.renderBackground(graphics, view);
		StickyNoteViewRenderer.renderText(graphics, view);
		
		graphics.pose().popPose();
	}
	
	@SubscribeEvent
	private static void tick(ClientTickEvent.Post event)
	{
		var level = Minecraft.getInstance().level;
		if(level == null)
		{
			return;
		}
		var player = Minecraft.getInstance().player;
		CONTENTS = player.getData(LBRAttachments.NOTE_BOARD);
	}
}
