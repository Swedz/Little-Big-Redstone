package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.gui.logicconfig.LogicConfigMenu;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record ReturnToMicrochipMenuPacket() implements LBRCustomPacket
{
	public static final ReturnToMicrochipMenuPacket INSTANCE = new ReturnToMicrochipMenuPacket();
	
	public static final StreamCodec<ByteBuf, ReturnToMicrochipMenuPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		var player = (ServerPlayer) context.getPlayer();
		var playerName = player.getGameProfile().getName();
		
		if(player.containerMenu instanceof LogicConfigMenu menu)
		{
			menu.cancel(player);
		}
		else
		{
			LBR.LOGGER.warn("Received ReturnToMicrochipMenuPacket from {} when not in a logic config menu, discarding", playerName);
		}
	}
}
