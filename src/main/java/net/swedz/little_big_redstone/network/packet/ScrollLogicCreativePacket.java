package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record ScrollLogicCreativePacket(
		int containerId,
		int scrollRows
) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, ScrollLogicCreativePacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, ScrollLogicCreativePacket::containerId,
			ByteBufCodecs.VAR_INT, ScrollLogicCreativePacket::scrollRows,
			ScrollLogicCreativePacket::new
	);

	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();

		var player = (ServerPlayer) context.getPlayer();
		var playerName = player.getGameProfile().getName();

		if(!player.hasInfiniteMaterials())
		{
			LBR.LOGGER.warn("Received ScrollLogicCreativePacket from {} while not in creative mode, discarding", playerName);
			return;
		}

		if(player.hasContainerOpen() && player.containerMenu instanceof MicrochipMenu menu && menu.containerId == containerId)
		{
			menu.getLogicArrayItemHandler().creativeHandler().setScrollRows(scrollRows);
		}
		else
		{
			LBR.LOGGER.warn("Received ScrollLogicCreativePacket from {} when not in a microchip menu, discarding", playerName);
		}
	}
}
