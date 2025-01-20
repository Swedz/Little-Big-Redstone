package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.microchip.LogicSelectedPort;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record CreateMicrochipWirePacket(
		int containerId,
		int outputSlot, int outputPort,
		int inputSlot, int inputPort
) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, CreateMicrochipWirePacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, CreateMicrochipWirePacket::containerId,
			ByteBufCodecs.VAR_INT, CreateMicrochipWirePacket::outputSlot,
			ByteBufCodecs.VAR_INT, CreateMicrochipWirePacket::outputPort,
			ByteBufCodecs.VAR_INT, CreateMicrochipWirePacket::inputSlot,
			ByteBufCodecs.VAR_INT, CreateMicrochipWirePacket::inputPort,
			CreateMicrochipWirePacket::new
	);
	
	public CreateMicrochipWirePacket(int containerId, LogicSelectedPort output, LogicSelectedPort input)
	{
		this(containerId, output.entry().slot(), output.portIndex(), input.entry().slot(), input.portIndex());
	}
	
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
			var wires = microchip.wires();
			ItemStack heldItem = menu.getCarried();
			if(heldItem.is(LBRTags.Items.MICROCHIP_WIRE))
			{
				var outputLogic = components.get(outputSlot);
				var inputLogic = components.get(inputSlot);
				if(outputLogic != null && outputPort < outputLogic.component().outputs() &&
				   inputLogic != null && inputPort < inputLogic.component().inputs())
				{
					if(wires.add(outputSlot, outputPort, inputSlot, inputPort))
					{
						microchip.markDirty();
						heldItem.shrink(1);
					}
					else
					{
						LBR.LOGGER.info("Received CreateMicrochipWirePacket from {} with already existing wire: {}#{} -> {}#{}, discarding", playerName, outputSlot, outputPort, inputSlot, inputPort);
					}
				}
				else
				{
					LBR.LOGGER.warn("Received CreateMicrochipWirePacket from {} with invalid output/input parameters: {}#{} -> {}#{}, discarding", playerName, outputSlot, outputPort, inputSlot, inputPort);
				}
			}
			else
			{
				LBR.LOGGER.warn("Received CreateMicrochipWirePacket from {} without holding a microchip wire item, discarding", playerName);
			}
		}
		else
		{
			LBR.LOGGER.warn("Received CreateMicrochipWirePacket from {} while not in a microchip menu (or in expired one?), discarding", playerName);
		}
	}
}
