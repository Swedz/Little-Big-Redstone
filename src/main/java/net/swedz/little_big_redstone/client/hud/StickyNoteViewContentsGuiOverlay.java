package net.swedz.little_big_redstone.client.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClient;
import net.swedz.little_big_redstone.client.StickyNoteViewRenderer;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.little_big_redstone.network.packet.RequestStickyNoteWatcherPacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.Objects;

@EventBusSubscriber(modid = LBR.ID, value = Dist.CLIENT)
public final class StickyNoteViewContentsGuiOverlay
{
	private static boolean        SHOULD_FADE;
	private static StickyNoteView STICKY_NOTE;
	private static int            DISPLAY_TIME;
	
	public static void update(StickyNoteView stickyNote)
	{
		if((stickyNote == null || stickyNote.text().equals(Component.empty())) && STICKY_NOTE != null)
		{
			SHOULD_FADE = true;
		}
		else if(stickyNote == null || !stickyNote.text().equals(Component.empty()))
		{
			SHOULD_FADE = false;
			STICKY_NOTE = stickyNote;
			DISPLAY_TIME = LBRClient.config().stickyNoteInWorldViewLingerTime();
		}
	}
	
	public static void render(GuiGraphics internal, DeltaTracker delta)
	{
		// If a screen is open, stop viewing the sticky note
		if(Minecraft.getInstance().screen != null)
		{
			var proxy = Proxies.get(LBRProxy.class);
			var watchedStickyNote = proxy.getWatchedStickyNote();
			proxy.updateWatchedStickyNote(null);
			if(watchedStickyNote != null)
			{
				new RequestStickyNoteWatcherPacket(null, false).sendToServer();
			}
			return;
		}
		
		if(STICKY_NOTE != null)
		{
			int alpha = Math.min((int) ((DISPLAY_TIME - delta.getGameTimeDeltaPartialTick(false)) * (255f / 20f)), 255);
			if(alpha > 8)
			{
				var graphics = new TesseractGuiGraphics(internal);
				
				float scale = (float) LBRClient.config().stickyNoteInWorldViewScale();
				
				graphics.pose().pushPose();
				graphics.pose().translate(10, 10, 0);
				graphics.pose().scale(scale, scale, 1);
				
				StickyNoteViewRenderer.renderBackground(graphics, STICKY_NOTE, alpha / 255f);
				StickyNoteViewRenderer.renderText(graphics, STICKY_NOTE, alpha / 255f);
				
				graphics.pose().popPose();
			}
		}
	}
	
	private static Integer LAST_TARGET_ENTITY_ID;
	
	@SubscribeEvent
	private static void tick(ClientTickEvent.Post event)
	{
		var level = Minecraft.getInstance().level;
		if(level == null)
		{
			return;
		}
		var proxy = Proxies.get(LBRProxy.class);
		var player = Minecraft.getInstance().player;
		var targetEntity = Minecraft.getInstance().hitResult instanceof EntityHitResult hitResult ? hitResult.getEntity() : null;
		Integer targetEntityId = targetEntity != null && Minecraft.getInstance().screen == null ? targetEntity.getId() : null;
		var watchedStickyNote = proxy.getWatchedStickyNote();
		
		if(!Objects.equals(targetEntityId, LAST_TARGET_ENTITY_ID))
		{
			proxy.updateWatchedStickyNote(null);
			if(targetEntity instanceof StickyNoteEntity stickyNote)
			{
				new RequestStickyNoteWatcherPacket(targetEntityId, true).sendToServer();
			}
			else if(watchedStickyNote != null)
			{
				new RequestStickyNoteWatcherPacket(targetEntityId, false).sendToServer();
			}
		}
		
		if(SHOULD_FADE)
		{
			if(DISPLAY_TIME > 0)
			{
				DISPLAY_TIME--;
			}
			else
			{
				STICKY_NOTE = null;
				SHOULD_FADE = false;
			}
		}
		
		LAST_TARGET_ENTITY_ID = targetEntityId;
	}
}
