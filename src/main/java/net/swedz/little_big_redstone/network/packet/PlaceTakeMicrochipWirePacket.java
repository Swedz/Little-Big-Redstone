package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.microchip.wire.PortReference;
import net.swedz.little_big_redstone.microchip.wire.Wire;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record PlaceTakeMicrochipWirePacket(
		int containerId,
		int outputSlot, int outputPort,
		int inputSlot, int inputPort,
		boolean place
) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, PlaceTakeMicrochipWirePacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, PlaceTakeMicrochipWirePacket::containerId,
			ByteBufCodecs.VAR_INT, PlaceTakeMicrochipWirePacket::outputSlot,
			ByteBufCodecs.VAR_INT, PlaceTakeMicrochipWirePacket::outputPort,
			ByteBufCodecs.VAR_INT, PlaceTakeMicrochipWirePacket::inputSlot,
			ByteBufCodecs.VAR_INT, PlaceTakeMicrochipWirePacket::inputPort,
			ByteBufCodecs.BOOL, PlaceTakeMicrochipWirePacket::place,
			PlaceTakeMicrochipWirePacket::new
	);
	
	public PlaceTakeMicrochipWirePacket(int containerId, PortReference output, PortReference input, boolean place)
	{
		this(containerId, output.slot(), output.index(), input.slot(), input.index(), place);
	}
	
	public PlaceTakeMicrochipWirePacket(int containerId, Wire wire, boolean place)
	{
		this(containerId, wire.output().slot(), wire.output().index(), wire.input().slot(), wire.input().index(), place);
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
			if(heldItem.is(LBRItems.REDSTONE_BIT.asItem()))
			{
				var outputLogic = components.get(outputSlot);
				var inputLogic = components.get(inputSlot);
				if(outputLogic != null && outputPort < outputLogic.component().outputs() &&
				   inputLogic != null && inputPort < inputLogic.component().inputs())
				{
					if(place)
					{
						if(wires.add(outputSlot, outputPort, inputSlot, inputPort))
						{
							microchip.markDirty();
							heldItem.shrink(1);
						}
						else
						{
							LBR.LOGGER.warn("Received PlaceTakeMicrochipWirePacket from {} with already existing wire: {}#{} -> {}#{}, discarding", playerName, outputSlot, outputPort, inputSlot, inputPort);
						}
					}
					else
					{
						var wire = wires.get(outputSlot, outputPort, inputSlot, inputPort);
						if(wire != null)
						{
							wires.remove(wire);
							microchip.markDirty();
							heldItem.grow(1);
						}
						else
						{
							LBR.LOGGER.warn("Received PlaceTakeMicrochipWirePacket from {} for non-existing wire: {}#{} -> {}#{}, discarding", playerName, outputSlot, outputPort, inputSlot, inputPort);
						}
					}
				}
				else
				{
					LBR.LOGGER.warn("Received PlaceTakeMicrochipWirePacket from {} with invalid output/input parameters: {}#{} -> {}#{}, discarding", playerName, outputSlot, outputPort, inputSlot, inputPort);
				}
			}
			else
			{
				if(place)
				{
					LBR.LOGGER.warn("Received PlaceTakeMicrochipWirePacket from {} without holding a microchip wire item, discarding", playerName);
				}
				else
				{
					var wire = wires.get(outputSlot, outputPort, inputSlot, inputPort);
					if(wire != null)
					{
						wires.remove(wire);
						microchip.markDirty();
						menu.setCarried(LBRItems.REDSTONE_BIT.asItem().getDefaultInstance());
					}
					else
					{
						LBR.LOGGER.warn("Received PlaceTakeMicrochipWirePacket from {} for non-existing wire: {}#{} -> {}#{}, discarding", playerName, outputSlot, outputPort, inputSlot, inputPort);
					}
				}
			}
		}
		else
		{
			LBR.LOGGER.warn("Received PlaceTakeMicrochipWirePacket from {} while not in a microchip menu (or in expired one?), discarding", playerName);
		}
	}
}
