package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.gui.logicconfig.LogicConfigMenu;
import net.swedz.little_big_redstone.gui.logicconfig.reference.MicrochipLogicConfigReference;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.gui.microchip.MicrochipViewPosition;
import net.swedz.little_big_redstone.microchip.object.logic.LogicCodecs;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record OpenLogicConfigPacket(
		int containerId,
		int slot,
		MicrochipViewPosition returnViewPosition
) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, OpenLogicConfigPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, OpenLogicConfigPacket::containerId,
			ByteBufCodecs.VAR_INT, OpenLogicConfigPacket::slot,
			MicrochipViewPosition.STREAM_CODEC, OpenLogicConfigPacket::returnViewPosition,
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
			var blockEntity = player.level().getBlockEntity(pos);
			if(blockEntity instanceof MicrochipBlockEntity)
			{
				var microchip = menu.microchip();
				var entry = microchip.components().get(slot);
				if(entry != null && entry.component().config().hasMenu())
				{
					var logicConfig = entry.component().config();
					var logicColor = entry.color().orElse(menu.color());
					
					player.openMenu(
							new MenuProvider()
							{
								@Override
								public Component getDisplayName()
								{
									return logicConfig.type().displayName();
								}
								
								@Override
								public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player)
								{
									return new LogicConfigMenu(
											containerId,
											playerInventory,
											false,
											new MicrochipLogicConfigReference(pos, entry.slot(), returnViewPosition),
											() -> menu.stillValid(player) && microchip.components().values().contains(entry),
											logicColor,
											logicConfig
									);
								}
							},
							(buf) ->
							{
								ByteBufCodecs.BOOL.encode(buf, false);
								DyeColor.STREAM_CODEC.encode(buf, logicColor);
								LogicCodecs.CONFIG_STREAM_CODEC.encode(buf, logicConfig);
							}
					);
				}
				else
				{
					LBR.LOGGER.warn("Received OpenLogicConfigPacket from {} targetting mismatching or non-existent component or a component with no config menu (slot {})", playerName, slot);
				}
			}
			else
			{
				LBR.LOGGER.warn("Received OpenLogicConfigPacket from {} for non-existent block entity?, discarding", playerName);
			}
		}
		else
		{
			LBR.LOGGER.warn("Received OpenLogicConfigPacket from {} while not in a microchip menu (or in expired one?), discarding", playerName);
		}
	}
}
