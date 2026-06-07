package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRAttachments;
import net.swedz.little_big_redstone.gui.noteboard.NoteBoardMenu;
import net.swedz.little_big_redstone.gui.noteboard.contents.NoteBoardStickyNote;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record PlaceTakeNoteBoardStickyNotePacket(
		int containerId,
		int index,
		float x,
		float y,
		int size,
		boolean place
) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, PlaceTakeNoteBoardStickyNotePacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, PlaceTakeNoteBoardStickyNotePacket::containerId,
			ByteBufCodecs.VAR_INT, PlaceTakeNoteBoardStickyNotePacket::index,
			ByteBufCodecs.FLOAT, PlaceTakeNoteBoardStickyNotePacket::x,
			ByteBufCodecs.FLOAT, PlaceTakeNoteBoardStickyNotePacket::y,
			ByteBufCodecs.VAR_INT, PlaceTakeNoteBoardStickyNotePacket::size,
			ByteBufCodecs.BOOL, PlaceTakeNoteBoardStickyNotePacket::place,
			PlaceTakeNoteBoardStickyNotePacket::new
	);
	
	public static PlaceTakeNoteBoardStickyNotePacket place(int containerId, float x, float y, int size)
	{
		return new PlaceTakeNoteBoardStickyNotePacket(containerId, -1, x, y, size, true);
	}
	
	public static PlaceTakeNoteBoardStickyNotePacket take(int containerId, int index)
	{
		return new PlaceTakeNoteBoardStickyNotePacket(containerId, index, -1, -1, -1, false);
	}
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		var player = (ServerPlayer) context.getPlayer();
		var playerName = player.getGameProfile().name();
		
		if(player.containerMenu instanceof NoteBoardMenu menu &&
		   menu.containerId == containerId)
		{
			var carried = menu.getCarried();
			var contents = player.getData(LBRAttachments.NOTE_BOARD);
			
			if(place)
			{
				if(StickyNoteItem.hasRelevantComponents(carried))
				{
					if(x >= 0 && x <= 1 && y >= 0 && y <= 1)
					{
						if(NoteBoardStickyNote.validateSize(size))
						{
							var stack = carried.copyWithCount(1);
							carried.shrink(1);
							
							contents = contents.add(new NoteBoardStickyNote(x, y, size, stack));
							player.setData(LBRAttachments.NOTE_BOARD, contents);
						}
						else
						{
							LBR.LOGGER.warn("Received PlaceTakeNoteBoardStickyNotePacket from {} with an invalid size ({}), discarding", playerName, size);
						}
					}
					else
					{
						LBR.LOGGER.warn("Received PlaceTakeNoteBoardStickyNotePacket from {} with an invalid position ({}, {}), discarding", playerName, x, y);
					}
				}
				else
				{
					LBR.LOGGER.warn("Received PlaceTakeNoteBoardStickyNotePacket from {} while not holding a sticky note, discarding", playerName);
				}
			}
			else
			{
				if(index >= 0 && index < contents.size())
				{
					var clickedNote = contents.get(index);
					if(carried.isEmpty() ||
					   ItemStack.isSameItemSameComponents(carried, clickedNote.stack()))
					{
						contents = contents.remove(index);
						player.setData(LBRAttachments.NOTE_BOARD, contents);
						
						if(carried.isEmpty())
						{
							menu.setCarried(clickedNote.stack().copy());
						}
						else
						{
							carried.grow(clickedNote.stack().getCount());
						}
					}
					else
					{
						LBR.LOGGER.warn("Received PlaceTakeNoteBoardStickyNotePacket from {} trying to pick up a note without valid space in the cursor, discarding", playerName);
					}
				}
				else
				{
					LBR.LOGGER.warn("Received PlaceTakeNoteBoardStickyNotePacket from {} trying to pick up a non-existent note, discarding", playerName);
				}
			}
		}
		else
		{
			LBR.LOGGER.warn("Received PlaceTakeNoteBoardStickyNotePacket from {} while not in the inventory menu (or in expired one?), discarding", playerName);
		}
	}
}
