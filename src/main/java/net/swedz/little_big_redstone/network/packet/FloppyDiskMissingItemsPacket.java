package net.swedz.little_big_redstone.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.packet.PacketContext;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.List;

public record FloppyDiskMissingItemsPacket(List<ItemStack> itemsMissing) implements LBRCustomPacket
{
	public static final StreamCodec<RegistryFriendlyByteBuf, FloppyDiskMissingItemsPacket> STREAM_CODEC = ItemStack.STREAM_CODEC
			.apply(ByteBufCodecs.list())
			.map(FloppyDiskMissingItemsPacket::new, FloppyDiskMissingItemsPacket::itemsMissing);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertClientbound();
		
		Proxies.get(LBRProxy.class).displayMissingItems(itemsMissing);
	}
}
