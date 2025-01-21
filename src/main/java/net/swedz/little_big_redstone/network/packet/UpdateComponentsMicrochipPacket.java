package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.LogicEntry;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.packet.PacketContext;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.List;

public record UpdateComponentsMicrochipPacket(int containerId, List<LogicEntry> entries) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, UpdateComponentsMicrochipPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, UpdateComponentsMicrochipPacket::containerId,
			LogicEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), UpdateComponentsMicrochipPacket::entries,
			UpdateComponentsMicrochipPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertClientbound();
		
		Proxies.get(LBRProxy.class).handleUpdateComponentsMicrochip(containerId, entries);
	}
}
