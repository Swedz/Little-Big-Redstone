package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

import java.util.Optional;

public record RequestStickyNoteWatcherPacket(Integer entityId, boolean start) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, RequestStickyNoteWatcherPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(ByteBufCodecs.INT).map((optional) -> optional.orElse(null), Optional::ofNullable), RequestStickyNoteWatcherPacket::entityId,
			ByteBufCodecs.BOOL, RequestStickyNoteWatcherPacket::start,
			RequestStickyNoteWatcherPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		if(start && entityId != null && context.getPlayer().level().getEntity(entityId) instanceof StickyNoteEntity entity)
		{
			new UpdateStickyNoteWatcherPacket(new StickyNoteView(entity)).sendToClient((ServerPlayer) context.getPlayer());
			context.getPlayer().setWatchedStickyNote(entityId);
		}
		else
		{
			context.getPlayer().setWatchedStickyNote(null);
		}
	}
}
