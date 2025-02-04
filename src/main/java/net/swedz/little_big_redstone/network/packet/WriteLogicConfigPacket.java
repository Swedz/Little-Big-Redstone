package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record WriteLogicConfigPacket(BlockPos pos, int slot, LogicComponent component) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, WriteLogicConfigPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, WriteLogicConfigPacket::pos,
			ByteBufCodecs.VAR_INT, WriteLogicConfigPacket::slot,
			LogicComponent.STREAM_CODEC, WriteLogicConfigPacket::component,
			WriteLogicConfigPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		var player = (ServerPlayer) context.getPlayer();
		var playerName = player.getGameProfile().getName();
		
		if(player.level().getBlockEntity(pos) instanceof MicrochipBlockEntity blockEntity)
		{
			var microchip = blockEntity.microchip();
			var targetComponent = microchip.components().get(slot);
			if(targetComponent != null && targetComponent.component().type().equals(component.type()))
			{
				targetComponent.component().config().loadFrom(component.config());
				microchip.components().updateValidity();
				int wiresPopped = microchip.wires().cleanup(targetComponent);
				if(wiresPopped > 0)
				{
					ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(LBRItems.REDSTONE_BIT, wiresPopped));
				}
				microchip.markDirty();
				
				blockEntity.openMenu(player);
			}
			else
			{
				LBR.LOGGER.warn("Received WriteLogicConfigPacket from {} targetting mismatching or non-existent component (slot {}), discarding", playerName, slot);
			}
		}
		else
		{
			LBR.LOGGER.warn("Received WriteLogicConfigPacket from {} when targeting non-microchip block entity (at {}), discarding", playerName, pos.toShortString());
		}
	}
}
