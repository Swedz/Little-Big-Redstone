package net.swedz.little_big_redstone.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.packet.PacketContext;
import net.swedz.tesseract.neoforge.proxy.Proxies;

public record UpdateStickyNoteWatcherPacket(StickyNoteView stickyNote) implements LBRCustomPacket
{
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateStickyNoteWatcherPacket> STREAM_CODEC = StreamCodec.composite(
			StickyNoteView.STREAM_CODEC, UpdateStickyNoteWatcherPacket::stickyNote,
			UpdateStickyNoteWatcherPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertClientbound();
		
		Proxies.get(LBRProxy.class).updateWatchedStickyNote(stickyNote);
	}
}
