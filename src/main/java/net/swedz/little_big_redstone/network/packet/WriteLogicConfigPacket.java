package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.gui.logicconfig.LogicConfigMenu;
import net.swedz.little_big_redstone.microchip.object.logic.LogicCodecs;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record WriteLogicConfigPacket(
		LogicConfig<?> config
) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, WriteLogicConfigPacket> STREAM_CODEC = StreamCodec.composite(
			LogicCodecs.CONFIG_STREAM_CODEC, WriteLogicConfigPacket::config,
			WriteLogicConfigPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		var player = (ServerPlayer) context.getPlayer();
		var playerName = player.getGameProfile().getName();
		
		if(player.containerMenu instanceof LogicConfigMenu menu)
		{
			menu.save(player, config);
		}
		else
		{
			LBR.LOGGER.warn("Received WriteLogicConfigPacket from {} when not in a logic config menu, discarding", playerName);
		}
	}
}
