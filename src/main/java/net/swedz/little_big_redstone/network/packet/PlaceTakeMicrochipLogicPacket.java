package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record PlaceTakeMicrochipLogicPacket(
		int containerId, int x, int y, boolean place, boolean leftClick, boolean shift
) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, PlaceTakeMicrochipLogicPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, PlaceTakeMicrochipLogicPacket::containerId,
			ByteBufCodecs.VAR_INT, PlaceTakeMicrochipLogicPacket::x,
			ByteBufCodecs.VAR_INT, PlaceTakeMicrochipLogicPacket::y,
			ByteBufCodecs.BOOL, PlaceTakeMicrochipLogicPacket::place,
			ByteBufCodecs.BOOL, PlaceTakeMicrochipLogicPacket::leftClick,
			ByteBufCodecs.BOOL, PlaceTakeMicrochipLogicPacket::shift,
			PlaceTakeMicrochipLogicPacket::new
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
			var components = microchip.components();
			ItemStack heldItem = menu.getCarried();
			if(place)
			{
				if(heldItem.has(LBRComponents.LOGIC))
				{
					var component = heldItem.get(LBRComponents.LOGIC);
					if(microchip.size().bounds().normalize().contains(component.size().toBounds(x, y)))
					{
						var logic = components.add(x, y, component);
						if(logic != null)
						{
							microchip.components().updateValidity();
							menu.placeCarriedWires(logic.slot());
							microchip.markDirty();
							if(!player.hasInfiniteMaterials() || leftClick)
							{
								heldItem.shrink(1);
							}
						}
						else
						{
							LBR.LOGGER.warn("Received PlaceTakeLogicPacket from {} and failed to add the logic component, discarding", playerName);
						}
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
					var logic = components.findAt(x, y);
					if(logic != null)
					{
						var wiresPopped = components.remove(logic);
						var stack = logic.toStack();
						if(!shift || !menu.moveItemStackTo(stack, 0, 36, true))
						{
							menu.setCarried(stack);
							menu.setCarriedWires(logic.slot(), wiresPopped);
						}
						else if(!player.hasInfiniteMaterials() && !wiresPopped.isEmpty())
						{
							ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(LBRItems.REDSTONE_BIT, wiresPopped.size()));
						}
						microchip.markDirty();
					}
					else
					{
						LBR.LOGGER.warn("Received PlaceTakeLogicPacket from {} with an invalid take position ({}, {}), discarding", playerName, x, y);
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
