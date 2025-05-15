package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.helper.CodecHelper;
import net.swedz.tesseract.neoforge.packet.PacketContext;
import net.swedz.tesseract.neoforge.proxy.Proxies;

public record StickyNotePacket(int entityId, Action action, String text) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, StickyNotePacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, StickyNotePacket::entityId,
			CodecHelper.forEnumStream(Action.class), StickyNotePacket::action,
			ByteBufCodecs.STRING_UTF8, StickyNotePacket::text,
			StickyNotePacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		if(action.isClientbound())
		{
			context.assertClientbound();
		}
		else
		{
			context.assertServerbound();
		}
		
		var player = context.getPlayer();
		var playerName = player.getGameProfile().getName();
		
		var entity = player.level().getEntity(entityId);
		if(entity instanceof StickyNoteEntity stickyNote)
		{
			if(action.isClientbound())
			{
				Proxies.get(LBRProxy.class).openStickyNote(entityId, stickyNote.getColor(), stickyNote.getTextColor(), text, action == Action.OPEN_EDIT);
			}
			else if(action == Action.DONE_EDIT)
			{
				if(entity.distanceTo(player) <= 16)
				{
					stickyNote.setNote(new StickyNote(text));
				}
				else
				{
					LBR.LOGGER.warn("Received StickyNotePacket from {} targeting a far away sticky note, discarding", playerName);
				}
			}
		}
		else
		{
			LBR.LOGGER.warn("Received StickyNotePacket from {} with an entity id ({}) targeting a non-sticky note entity, discarding", playerName, entityId);
		}
	}
	
	public enum Action
	{
		OPEN_VIEW(true),
		OPEN_EDIT(true),
		DONE_EDIT(false);
		
		private final boolean clientbound;
		
		Action(boolean clientbound)
		{
			this.clientbound = clientbound;
		}
		
		public boolean isClientbound()
		{
			return clientbound;
		}
	}
}
