package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.gui.microchip.MicrochipViewPosition;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record RequestMicrochipMenuPacket(BlockPos pos, MicrochipViewPosition viewPosition) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, RequestMicrochipMenuPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, RequestMicrochipMenuPacket::pos,
			MicrochipViewPosition.STREAM_CODEC, RequestMicrochipMenuPacket::viewPosition,
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
			if(!blockEntity.openMenu(player, viewPosition))
			{
				LBR.LOGGER.warn("Received WriteLogicConfigPacket from {} when too far or not in the same dimension or the block entity is removed, discarding", playerName);
			}
		}
		else
		{
			LBR.LOGGER.warn("Received WriteLogicConfigPacket from {} when targeting non-microchip block entity (at {}), discarding", playerName, pos.toShortString());
		}
	}
}
