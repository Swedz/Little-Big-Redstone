package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.packet.PacketContext;
import net.swedz.tesseract.neoforge.proxy.Proxies;

public record ForceFloppyDiskGuiOverlayUpdatePacket() implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, ForceFloppyDiskGuiOverlayUpdatePacket> STREAM_CODEC = StreamCodec.unit(new ForceFloppyDiskGuiOverlayUpdatePacket());
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertClientbound();
		
		Proxies.get(LBRProxy.class).forceFloppyDiskGuiOverlayUpdate();
	}
}
