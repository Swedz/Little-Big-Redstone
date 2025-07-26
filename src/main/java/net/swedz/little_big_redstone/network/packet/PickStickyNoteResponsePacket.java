package net.swedz.little_big_redstone.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.packet.PacketContext;
import net.swedz.tesseract.neoforge.proxy.Proxies;

public record PickStickyNoteResponsePacket(ItemStack stack) implements LBRCustomPacket
{
	public static final StreamCodec<RegistryFriendlyByteBuf, PickStickyNoteResponsePacket> STREAM_CODEC = ItemStack.STREAM_CODEC
			.map(PickStickyNoteResponsePacket::new, PickStickyNoteResponsePacket::stack);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertClientbound();
		
		Proxies.get(LBRProxy.class).setPickedItem(stack);
	}
}
