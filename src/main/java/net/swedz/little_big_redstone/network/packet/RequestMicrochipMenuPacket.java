package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.blockentity.MicrochipBlockEntity;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record RequestMicrochipMenuPacket(BlockPos pos) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, RequestMicrochipMenuPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, RequestMicrochipMenuPacket::pos,
			RequestMicrochipMenuPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		var player = (ServerPlayer) context.getPlayer();
		var playerName = player.getGameProfile().getName();
		
		if(player.level().getBlockEntity(pos) instanceof MicrochipBlockEntity blockEntity)
		{
			blockEntity.openMenu(player);
		}
		else
		{
			LBR.LOGGER.warn("Received WriteLogicConfigPacket from {} when targeting non-microchip block entity (at {}), discarding", playerName, pos.toShortString());
		}
	}
}
