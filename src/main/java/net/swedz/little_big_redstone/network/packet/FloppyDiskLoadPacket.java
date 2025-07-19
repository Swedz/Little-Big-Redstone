package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.helper.CodecHelper;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record FloppyDiskLoadPacket(
		InteractionHand hand,
		String name, Microchip.Immutable microchip
) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, FloppyDiskLoadPacket> STREAM_CODEC = StreamCodec.composite(
			CodecHelper.forEnumStream(InteractionHand.class), FloppyDiskLoadPacket::hand,
			ByteBufCodecs.STRING_UTF8, FloppyDiskLoadPacket::name,
			Microchip.Immutable.STREAM_CODEC, FloppyDiskLoadPacket::microchip,
			FloppyDiskLoadPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		var player = context.getPlayer();
		var stack = player.getItemInHand(hand);
		
		if(!stack.isEmpty() && stack.is(LBRTags.Items.FLOPPY_DISKS))
		{
			stack.set(LBRComponents.FLOPPY_DISK, microchip);
			player.sendSystemMessage(LBRText.FLOPPY_DISK_FILE_LOADED.text(name).withStyle(ChatFormatting.GREEN));
		}
		else
		{
			LBR.LOGGER.warn("Received FloppyDiskLoadPacket from {} without holding a floppy disk in hand", player.getName());
		}
	}
}
