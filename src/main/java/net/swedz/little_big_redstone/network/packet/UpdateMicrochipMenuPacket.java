package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.packet.PacketContext;
import net.swedz.tesseract.neoforge.proxy.Proxies;

public record UpdateMicrochipMenuPacket(
		int containerId,
		Microchip microchip,
		boolean rerouteWires
) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, UpdateMicrochipMenuPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, UpdateMicrochipMenuPacket::containerId,
			Microchip.STREAM_CODEC, UpdateMicrochipMenuPacket::microchip,
			ByteBufCodecs.BOOL, UpdateMicrochipMenuPacket::rerouteWires,
			UpdateMicrochipMenuPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertClientbound();
		
		Proxies.get(LBRProxy.class).handleUpdateMicrochip(containerId, microchip, rerouteWires);
	}
}
