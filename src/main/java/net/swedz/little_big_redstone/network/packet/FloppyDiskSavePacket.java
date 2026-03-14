package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.item.floppydisk.FloppyDiskProgramName;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.helper.CodecHelper;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record FloppyDiskSavePacket(
		InteractionHand hand,
		FloppyDiskProgramName name
) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, FloppyDiskSavePacket> STREAM_CODEC = StreamCodec.composite(
			CodecHelper.forEnumStream(InteractionHand.class), FloppyDiskSavePacket::hand,
			FloppyDiskProgramName.STREAM_CODEC, FloppyDiskSavePacket::name,
			FloppyDiskSavePacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		var player = context.getPlayer();
		var stack = player.getItemInHand(hand);
		
		if(!stack.isEmpty() && stack.is(LBRTags.Items.FLOPPY_DISKS))
		{
			stack.set(LBRComponents.FLOPPY_DISK_PROGRAM_NAME, name);
		}
		else
		{
			LBR.LOGGER.warn("Received FloppyDiskSavePacket from {} without holding a floppy disk in hand", player.getName());
		}
	}
}
