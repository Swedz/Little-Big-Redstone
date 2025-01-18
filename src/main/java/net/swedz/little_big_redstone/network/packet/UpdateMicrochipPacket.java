package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record UpdateMicrochipPacket(Microchip microchip) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, UpdateMicrochipPacket> STREAM_CODEC = Microchip.STREAM_CODEC.map(UpdateMicrochipPacket::new, UpdateMicrochipPacket::microchip);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertClientbound();
		
		// TODO
	}
}
