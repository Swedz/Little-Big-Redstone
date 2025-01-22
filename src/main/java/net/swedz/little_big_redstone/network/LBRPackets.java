package net.swedz.little_big_redstone.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.network.packet.OpenLogicConfigPacket;
import net.swedz.little_big_redstone.network.packet.PlaceTakeMicrochipLogicPacket;
import net.swedz.little_big_redstone.network.packet.PlaceTakeMicrochipWirePacket;
import net.swedz.little_big_redstone.network.packet.UpdateComponentsMicrochipPacket;
import net.swedz.little_big_redstone.network.packet.UpdateMicrochipPacket;
import net.swedz.little_big_redstone.network.packet.WriteLogicConfigPacket;
import net.swedz.tesseract.neoforge.packet.PacketRegistry;

public final class LBRPackets
{
	private static final PacketRegistry<LBRCustomPacket> REGISTRY = PacketRegistry.create(LBR.ID);
	
	public static CustomPacketPayload.Type<LBRCustomPacket> getType(Class<? extends LBRCustomPacket> packetClass)
	{
		return REGISTRY.getType(packetClass);
	}
	
	public static void init(RegisterPayloadHandlersEvent event)
	{
		REGISTRY.registerAll(event);
	}
	
	static
	{
		create("open_logic_config", OpenLogicConfigPacket.class, OpenLogicConfigPacket.STREAM_CODEC);
		create("place_take_microchip_wire", PlaceTakeMicrochipWirePacket.class, PlaceTakeMicrochipWirePacket.STREAM_CODEC);
		create("place_take_microchip_logic", PlaceTakeMicrochipLogicPacket.class, PlaceTakeMicrochipLogicPacket.STREAM_CODEC);
		create("update_components_microchip", UpdateComponentsMicrochipPacket.class, UpdateComponentsMicrochipPacket.STREAM_CODEC);
		create("update_microchip", UpdateMicrochipPacket.class, UpdateMicrochipPacket.STREAM_CODEC);
		create("write_logic_config", WriteLogicConfigPacket.class, WriteLogicConfigPacket.STREAM_CODEC);
	}
	
	private static <P extends LBRCustomPacket> void create(String id, Class<P> packetClass, StreamCodec<? super RegistryFriendlyByteBuf, P> packetCodec)
	{
		REGISTRY.create(id, packetClass, packetCodec);
	}
}
