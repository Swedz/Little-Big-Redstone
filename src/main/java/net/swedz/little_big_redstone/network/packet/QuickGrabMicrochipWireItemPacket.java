package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.helper.TransferHelper;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record QuickGrabMicrochipWireItemPacket(int containerId) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, QuickGrabMicrochipWireItemPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, QuickGrabMicrochipWireItemPacket::containerId,
			QuickGrabMicrochipWireItemPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		var player = (ServerPlayer) context.getPlayer();
		var playerName = player.getGameProfile().getName();
		
		if(player.hasContainerOpen() && player.containerMenu instanceof MicrochipMenu menu && menu.containerId == containerId)
		{
			var carried = menu.getCarried();
			
			if(carried.isEmpty())
			{
				var extracted = TransferHelper.extractMatching(menu.getLogicArrayItemHandler(), (stack) -> stack.is(LBRItems.REDSTONE_BIT.asItem()), 1);
				if(!extracted.isEmpty())
				{
					menu.setCarried(extracted);
				}
				else
				{
					LBR.LOGGER.warn("Received QuickGrabMicrochipWireItemPacket from {} with no wire item to pull from the logic array", playerName);
				}
			}
			else
			{
				LBR.LOGGER.warn("Received QuickGrabMicrochipWireItemPacket from {} when carrying an item in the cursor", playerName);
			}
		}
		else
		{
			LBR.LOGGER.warn("Received QuickGrabMicrochipWireItemPacket from {} while not in a microchip menu (or in expired one?), discarding", playerName);
		}
	}
}
