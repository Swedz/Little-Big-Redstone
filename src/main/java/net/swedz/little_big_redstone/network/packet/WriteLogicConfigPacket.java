package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.gui.logicconfig.LogicConfigMenu;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record WriteLogicConfigPacket(LogicComponent component) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, WriteLogicConfigPacket> STREAM_CODEC = StreamCodec.composite(
			LogicComponent.STREAM_CODEC, WriteLogicConfigPacket::component,
			WriteLogicConfigPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		var player = (ServerPlayer) context.getPlayer();
		var playerName = player.getGameProfile().name();
		
		if(player.containerMenu instanceof LogicConfigMenu menu)
		{
			menu.save(player, component);
		}
		else
		{
			LBR.LOGGER.warn("Received WriteLogicConfigPacket from {} when not in a logic config menu, discarding", playerName);
		}
	}
}
