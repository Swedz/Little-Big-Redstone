package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.gui.logicconfig.LogicConfigMenu;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.microchip.LogicEntry;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record OpenLogicConfigPacket(int containerId, int slot) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, OpenLogicConfigPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, OpenLogicConfigPacket::containerId,
			ByteBufCodecs.VAR_INT, OpenLogicConfigPacket::slot,
			OpenLogicConfigPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		var player = context.getPlayer();
		var playerName = player.getGameProfile().getName();
		
		if(player.hasContainerOpen() && player.containerMenu instanceof MicrochipMenu menu && menu.containerId == containerId)
		{
			var pos = menu.blockPos();
			var microchip = menu.microchip();
			var entry = microchip.components().get(slot);
			if(entry != null)
			{
				var blockEntity = player.level().getBlockEntity(pos);
				if(blockEntity != null)
				{
					player.openMenu(
							new MenuProvider()
							{
								@Override
								public Component getDisplayName()
								{
									return entry.component().type().displayName();
								}
								
								@Override
								public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player)
								{
									return new LogicConfigMenu(containerId, playerInventory, menu.blockPos(), () -> !blockEntity.isRemoved() && microchip.components().values().contains(entry), entry);
								}
							},
							(buf) ->
							{
								buf.writeBlockPos(menu.blockPos());
								LogicEntry.STREAM_CODEC.encode(buf, entry);
							}
					);
				}
				else
				{
					LBR.LOGGER.warn("Received OpenLogicConfigPacket from {} for non-existent block entity?, discarding", playerName);
				}
			}
			else
			{
				LBR.LOGGER.warn("Received OpenLogicConfigPacket from {} targetting mismatching or non-existent component (slot {})", playerName, slot);
			}
		}
		else
		{
			LBR.LOGGER.warn("Received OpenLogicConfigPacket from {} while not in a microchip menu (or in expired one?), discarding", playerName);
		}
	}
}
