package net.swedz.little_big_redstone.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;
import net.swedz.little_big_redstone.network.packet.PickStickyNotePacket;

/**
 * Because the entity/block picking process in vanilla is entirely client sided, and the client is not aware of the
 * text content of a sticky note (to avoid excess network and memory load), the client has to request the server to
 * give the sticky note item to it.
 */
@EventBusSubscriber(modid = LBR.ID, value = Dist.CLIENT)
public final class StickyNoteEntityPicker
{
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	private static void onMiddleClick(InputEvent.InteractionKeyMappingTriggered event)
	{
		if(event.isPickBlock())
		{
			var minecraft = Minecraft.getInstance();
			var player = minecraft.player;
			var hitResult = minecraft.hitResult;
			boolean creative = player.getAbilities().instabuild;
			
			if(hitResult.getType() == HitResult.Type.ENTITY && creative &&
			   ((EntityHitResult) hitResult).getEntity() instanceof StickyNoteEntity entity)
			{
				event.setCanceled(true);
				
				new PickStickyNotePacket(entity.getId(), minecraft.hasControlDown()).sendToServer();
			}
		}
	}
}
