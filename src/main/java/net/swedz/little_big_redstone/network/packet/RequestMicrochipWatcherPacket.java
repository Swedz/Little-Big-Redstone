package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record RequestMicrochipWatcherPacket(BlockPos pos, boolean start) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, RequestMicrochipWatcherPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, RequestMicrochipWatcherPacket::pos,
			ByteBufCodecs.BOOL, RequestMicrochipWatcherPacket::start,
			RequestMicrochipWatcherPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		if(start && context.getPlayer().level().getBlockEntity(pos) instanceof MicrochipBlockEntity microchipBlockEntity)
		{
			new UpdateMicrochipWatcherPacket(microchipBlockEntity.microchip()).sendToClient((ServerPlayer) context.getPlayer());
			context.getPlayer().setWatchedMicrochip(pos);
		}
		else
		{
			context.getPlayer().setWatchedMicrochip(null);
		}
	}
}
