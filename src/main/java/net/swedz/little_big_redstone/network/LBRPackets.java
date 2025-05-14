package net.swedz.little_big_redstone.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.network.packet.DyeMicrochipObjectPacket;
import net.swedz.little_big_redstone.network.packet.ForceFloppyDiskGuiOverlayUpdatePacket;
import net.swedz.little_big_redstone.network.packet.OpenLogicConfigPacket;
import net.swedz.little_big_redstone.network.packet.PlaceTakeMicrochipObjectPacket;
import net.swedz.little_big_redstone.network.packet.PlaceTakeMicrochipWirePacket;
import net.swedz.little_big_redstone.network.packet.RequestMicrochipMenuPacket;
import net.swedz.little_big_redstone.network.packet.StickyNotePacket;
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
		create("dye_microchip_object", DyeMicrochipObjectPacket.class, DyeMicrochipObjectPacket.STREAM_CODEC);
		create("force_floppy_disk_gui_overlay_update", ForceFloppyDiskGuiOverlayUpdatePacket.class, ForceFloppyDiskGuiOverlayUpdatePacket.STREAM_CODEC);
		create("open_logic_config", OpenLogicConfigPacket.class, OpenLogicConfigPacket.STREAM_CODEC);
		create("place_take_microchip_object", PlaceTakeMicrochipObjectPacket.class, PlaceTakeMicrochipObjectPacket.STREAM_CODEC);
		create("place_take_microchip_wire", PlaceTakeMicrochipWirePacket.class, PlaceTakeMicrochipWirePacket.STREAM_CODEC);
		create("request_microchip_menu", RequestMicrochipMenuPacket.class, RequestMicrochipMenuPacket.STREAM_CODEC);
		create("sticky_note", StickyNotePacket.class, StickyNotePacket.STREAM_CODEC);
		create("update_components_microchip", UpdateComponentsMicrochipPacket.class, UpdateComponentsMicrochipPacket.STREAM_CODEC);
		create("update_microchip", UpdateMicrochipPacket.class, UpdateMicrochipPacket.STREAM_CODEC);
		create("write_logic_config", WriteLogicConfigPacket.class, WriteLogicConfigPacket.STREAM_CODEC);
	}
	
	private static <P extends LBRCustomPacket> void create(String id, Class<P> packetClass, StreamCodec<? super RegistryFriendlyByteBuf, P> packetCodec)
	{
		REGISTRY.create(id, packetClass, packetCodec);
	}
}
