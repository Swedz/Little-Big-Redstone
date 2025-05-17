package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.packet.PacketContext;
import net.swedz.tesseract.neoforge.proxy.Proxies;

public record FloppyDiskGuiOverlayUpdatePacket(boolean force) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, FloppyDiskGuiOverlayUpdatePacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, FloppyDiskGuiOverlayUpdatePacket::force,
			FloppyDiskGuiOverlayUpdatePacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertClientbound();
		
		Proxies.get(LBRProxy.class).floppyDiskGuiOverlayUpdate(force);
	}
}
