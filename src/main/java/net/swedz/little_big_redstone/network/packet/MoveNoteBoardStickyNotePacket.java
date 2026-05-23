package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRAttachments;
import net.swedz.little_big_redstone.gui.noteboard.NoteBoardMenu;
import net.swedz.little_big_redstone.gui.noteboard.contents.NoteBoardStickyNote;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record MoveNoteBoardStickyNotePacket(
		int containerId,
		int index,
		float x,
		float y,
		int size
) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, MoveNoteBoardStickyNotePacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, MoveNoteBoardStickyNotePacket::containerId,
			ByteBufCodecs.VAR_INT, MoveNoteBoardStickyNotePacket::index,
			ByteBufCodecs.FLOAT, MoveNoteBoardStickyNotePacket::x,
			ByteBufCodecs.FLOAT, MoveNoteBoardStickyNotePacket::y,
			ByteBufCodecs.VAR_INT, MoveNoteBoardStickyNotePacket::size,
			MoveNoteBoardStickyNotePacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		var player = (ServerPlayer) context.getPlayer();
		var playerName = player.getGameProfile().getName();
		
		if(player.containerMenu instanceof NoteBoardMenu menu &&
		   menu.containerId == containerId)
		{
			if(menu.getCarried().isEmpty())
			{
				var noteBoard = player.getData(LBRAttachments.NOTE_BOARD);
				if(noteBoard.has(index))
				{
					var note = noteBoard.get(index);
					if(x >= 0 && x <= 1 && y >= 0 && y <= 1)
					{
						if(NoteBoardStickyNote.validateSize(size))
						{
							noteBoard = noteBoard.update(index, note.moveTo(x, y, size));
							player.setData(LBRAttachments.NOTE_BOARD, noteBoard);
						}
						else
						{
							LBR.LOGGER.warn("Received MoveNoteBoardStickyNotePacket from {} with an invalid size ({}), discarding", playerName, size);
						}
					}
					else
					{
						LBR.LOGGER.warn("Received MoveNoteBoardStickyNotePacket from {} with an invalid position ({}, {}), discarding", playerName, x, y);
					}
				}
				else
				{
					LBR.LOGGER.warn("Received MoveNoteBoardStickyNotePacket from {} for an invalid note index, discarding", playerName);
				}
			}
			else
			{
				LBR.LOGGER.warn("Received MoveNoteBoardStickyNotePacket from {} while holding an item in cursor, discarding", playerName);
			}
		}
		else
		{
			LBR.LOGGER.warn("Received MoveNoteBoardStickyNotePacket from {} while not in the inventory menu (or in expired one?), discarding", playerName);
		}
	}
}
