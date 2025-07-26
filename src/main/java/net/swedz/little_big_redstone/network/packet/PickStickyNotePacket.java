package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record PickStickyNotePacket(int entityId, boolean includeData) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, PickStickyNotePacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, PickStickyNotePacket::entityId,
			ByteBufCodecs.BOOL, PickStickyNotePacket::includeData,
			PickStickyNotePacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		var player = (ServerPlayer) context.getPlayer();
		var playerName = player.getGameProfile().getName();
		var level = player.level();
		
		if(player.getAbilities().instabuild)
		{
			if(level.getEntity(entityId) instanceof StickyNoteEntity entity)
			{
				var stack = entity.asItem(includeData);
				new PickStickyNoteResponsePacket(stack).sendToClient(player);
			}
			else
			{
				LBR.LOGGER.warn("Received PickStickyNotePacket from {} targeting an entity id that is not a sticky note, discarding", playerName);
			}
		}
		else
		{
			LBR.LOGGER.warn("Received PickStickyNotePacket from {} while not in creative mode, discarding", playerName);
		}
	}
}
