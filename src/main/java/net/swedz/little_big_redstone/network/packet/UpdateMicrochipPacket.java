package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.packet.PacketContext;
import net.swedz.tesseract.neoforge.proxy.Proxies;

public record UpdateMicrochipPacket(int containerId, Microchip microchip) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, UpdateMicrochipPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, UpdateMicrochipPacket::containerId,
			Microchip.STREAM_CODEC, UpdateMicrochipPacket::microchip,
			UpdateMicrochipPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertClientbound();
		
		Proxies.get(LBRProxy.class).handleUpdateMicrochip(containerId, microchip);
	}
}
