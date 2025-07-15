package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.packet.PacketContext;
import net.swedz.tesseract.neoforge.proxy.Proxies;

public record UpdateMicrochipWatcherPacket(Microchip microchip) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, UpdateMicrochipWatcherPacket> STREAM_CODEC = StreamCodec.composite(
			Microchip.STREAM_CODEC, UpdateMicrochipWatcherPacket::microchip,
			UpdateMicrochipWatcherPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertClientbound();
		
		Proxies.get(LBRProxy.class).updateWatchedMicrochip(microchip);
	}
}
