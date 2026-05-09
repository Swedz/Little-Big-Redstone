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

public record StoreMicrochipViewPositionPacket(
		BlockPos pos,
		MicrochipViewPosition viewPosition
) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, StoreMicrochipViewPositionPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, StoreMicrochipViewPositionPacket::pos,
			MicrochipViewPosition.STREAM_CODEC, StoreMicrochipViewPositionPacket::viewPosition,
			StoreMicrochipViewPositionPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		var player = (ServerPlayer) context.getPlayer();
		var playerName = player.getGameProfile().getName();
		
		if(player.level().getBlockEntity(pos) instanceof MicrochipBlockEntity blockEntity)
		{
			blockEntity.setViewPosition(viewPosition);
		}
		else
		{
			LBR.LOGGER.warn("Received StoreMicrochipViewPositionPacket from {} when not in a microchip menu, discarding", playerName);
		}
	}
}
