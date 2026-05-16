package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.gui.microchip.logic.DyeComponentResult;
import net.swedz.little_big_redstone.microchip.object.MicrochipObjectContainerType;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.helper.CodecHelper;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record DyeMicrochipObjectPacket(
		int containerId, MicrochipObjectContainerType containerType, int slot
) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, DyeMicrochipObjectPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, DyeMicrochipObjectPacket::containerId,
			CodecHelper.forEnumStream(MicrochipObjectContainerType.class), DyeMicrochipObjectPacket::containerType,
			ByteBufCodecs.VAR_INT, DyeMicrochipObjectPacket::slot,
			DyeMicrochipObjectPacket::new
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
			var entry = microchip.get(slot, containerType);
			if(entry != null)
			{
				var result = DyeComponentResult.test(heldItem, entry.color());
				if(result.success())
				{
					if(entry.setColor(result.color()))
					{
						microchip.markDirty(false);
						if(result.consume())
						{
							heldItem.consume(1, player);
						}
					}
					else
					{
						LBR.LOGGER.warn("Received DyeMicrochipObjectPacket from {} when the color of the entry remains unchanged, discarding", playerName);
					}
				}
				else if(result.result() == DyeComponentResult.Result.WRONG_ITEM)
				{
					LBR.LOGGER.warn("Received DyeMicrochipObjectPacket from {} while not carrying a dye or dye washing item in their cursor, discarding", playerName);
				}
				else if(result.result() == DyeComponentResult.Result.NO_CHANGE)
				{
					LBR.LOGGER.warn("Received DyeMicrochipObjectPacket from {} when the color is already set to the clicked color", playerName);
				}
			}
			else
			{
				LBR.LOGGER.warn("Received DyeMicrochipObjectPacket from {} targetting mismatching or non-existent component (slot {})", playerName, slot);
			}
		}
		else
		{
			LBR.LOGGER.warn("Received DyeMicrochipObjectPacket from {} while not in a microchip menu (or in expired one?), discarding", playerName);
		}
	}
}
