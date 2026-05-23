package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.swedz.little_big_redstone.gui.noteboard.NoteBoardMenu;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record OpenNoteBoardPacket() implements LBRCustomPacket
{
	public static final OpenNoteBoardPacket INSTANCE = new OpenNoteBoardPacket();
	
	public static final StreamCodec<ByteBuf, OpenNoteBoardPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		var player = context.getPlayer();
		
		player.openMenu(new MenuProvider()
		{
			@Override
			public Component getDisplayName()
			{
				return Component.empty();
			}
			
			@Override
			public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
			{
				return new NoteBoardMenu(containerId, inventory);
			}
		});
	}
}
