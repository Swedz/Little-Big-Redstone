package net.swedz.little_big_redstone.client.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRAttachments;
import net.swedz.little_big_redstone.client.StickyNoteViewRenderer;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.little_big_redstone.gui.noteboard.contents.NoteBoardContents;

@EventBusSubscriber(modid = LBR.ID, value = Dist.CLIENT)
public final class NoteBoardGuiOverlay
{
	private static NoteBoardContents CONTENTS = NoteBoardContents.EMPTY;
	
	public static void extract(GuiGraphicsExtractor graphics, DeltaTracker delta)
	{
		if(Minecraft.getInstance().options.hideGui)
		{
			return;
		}
		
		for(var note : CONTENTS)
		{
			renderNote(graphics, note.x(graphics.guiWidth()), note.y(graphics.guiHeight()), note.size(), note.asView());
		}
	}
	
	private static void renderNote(GuiGraphicsExtractor graphics, int x, int y, int size, StickyNoteView view)
	{
		graphics.pose().pushMatrix();
		graphics.pose().translate(x, y);
		float scale = size / 180f;
		graphics.pose().scale(scale, scale);
		
		StickyNoteViewRenderer.extractBackground(graphics, view);
		StickyNoteViewRenderer.extractText(graphics, view);
		
		graphics.pose().popMatrix();
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
