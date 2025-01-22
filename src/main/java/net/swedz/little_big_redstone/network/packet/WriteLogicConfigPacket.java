package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record WriteLogicConfigPacket(int containerId, int slot, LogicComponent component) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, WriteLogicConfigPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, WriteLogicConfigPacket::containerId,
			ByteBufCodecs.VAR_INT, WriteLogicConfigPacket::slot,
			LogicComponent.STREAM_CODEC, WriteLogicConfigPacket::component,
			WriteLogicConfigPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		// TODO
	}
}
