package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record PlaceTakeLogicPacket(int containerId, int x, int y, boolean place) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, PlaceTakeLogicPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, PlaceTakeLogicPacket::containerId,
			ByteBufCodecs.VAR_INT, PlaceTakeLogicPacket::x,
			ByteBufCodecs.VAR_INT, PlaceTakeLogicPacket::y,
			ByteBufCodecs.BOOL, PlaceTakeLogicPacket::place,
			PlaceTakeLogicPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		var player = (ServerPlayer) context.getPlayer();
		var playerName = player.getGameProfile().getName();
		
		if(player.hasContainerOpen() && player.containerMenu instanceof MicrochipMenu menu && menu.containerId == containerId)
		{
			var microchip = menu.microchip();
			ItemStack heldItem = menu.getCarried();
			if(place)
			{
				if(heldItem.has(LBRComponents.LOGIC))
				{
					var logic = heldItem.get(LBRComponents.LOGIC);
					if(microchip.add(x, y, logic))
					{
						heldItem.shrink(1);
					}
					else
					{
						LBR.LOGGER.warn("Received PlaceTakeLogicPacket from {} with an invalid placement position, discarding", playerName);
					}
				}
				else
				{
					LBR.LOGGER.warn("Received PlaceTakeLogicPacket from {} while not carrying a logic component in their cursor, discarding", playerName);
				}
			}
			else
			{
				if(heldItem.isEmpty())
				{
					var logic = microchip.findAt(x, y);
					if(logic != null)
					{
						microchip.remove(logic);
						menu.setCarried(logic.toStack());
					}
					else
					{
						LBR.LOGGER.warn("Received PlaceTakeLogicPacket from {} with an invalid take position, discarding", playerName);
					}
				}
				else
				{
					LBR.LOGGER.warn("Received PlaceTakeLogicPacket from {} while already carrying an item in their cursor, discarding", playerName);
				}
			}
		}
		else
		{
			LBR.LOGGER.warn("Received PlaceTakeLogicPacket from {} while not in a microchip menu (or in expired one?), discarding", playerName);
		}
	}
}
