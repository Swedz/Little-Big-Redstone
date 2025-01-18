package net.swedz.little_big_redstone.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.network.packet.PlaceTakeLogicPacket;
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
		create("place_take_logic", PlaceTakeLogicPacket.class, PlaceTakeLogicPacket.STREAM_CODEC);
	}
	
	private static <P extends LBRCustomPacket> void create(String id, Class<P> packetClass, StreamCodec<? super RegistryFriendlyByteBuf, P> packetCodec)
	{
		REGISTRY.create(id, packetClass, packetCodec);
	}
}
