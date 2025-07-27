package net.swedz.little_big_redstone.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.gui.stickynote.reference.EntityStickyNoteReference;
import net.swedz.little_big_redstone.gui.stickynote.reference.HeldItemStickyNoteReference;
import net.swedz.little_big_redstone.gui.stickynote.reference.MicrochipStickyNoteReference;
import net.swedz.little_big_redstone.gui.stickynote.reference.StickyNoteReference;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.network.LBRCustomPacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.helper.CodecHelper;
import net.swedz.tesseract.neoforge.packet.PacketContext;
import net.swedz.tesseract.neoforge.proxy.Proxies;

public record StickyNotePacket(
		ReferenceType referenceType, int data, Action action, String text
) implements LBRCustomPacket
{
	public static final StreamCodec<ByteBuf, StickyNotePacket> STREAM_CODEC = StreamCodec.composite(
			CodecHelper.forEnumStream(ReferenceType.class), StickyNotePacket::referenceType,
			ByteBufCodecs.VAR_INT, StickyNotePacket::data,
			CodecHelper.forEnumStream(Action.class), StickyNotePacket::action,
			ByteBufCodecs.STRING_UTF8, StickyNotePacket::text,
			StickyNotePacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		if(action.isClientbound())
		{
			context.assertClientbound();
		}
		else
		{
			context.assertServerbound();
		}
		
		var player = context.getPlayer();
		var reference = referenceType.create(player, data);
		if(reference != null)
		{
			reference = reference.withText(text);
			if(action.isClientbound())
			{
				Proxies.get(LBRProxy.class).openStickyNote(reference, action == Action.OPEN_EDIT);
			}
			else if(action == Action.DONE_EDIT)
			{
				reference.saveServer(player.level(), player);
			}
		}
	}
	
	public enum ReferenceType
	{
		ENTITY((player, data) -> player.level().getEntity(data) instanceof StickyNoteEntity entity ? new EntityStickyNoteReference(entity) : null),
		
		HELD_ITEM((player, data) ->
		{
			var hand = data == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
			var stack = player.getItemInHand(hand);
			return stack.getItem() instanceof StickyNoteItem ? new HeldItemStickyNoteReference(hand, stack) : null;
		}),
		
		MICROCHIP((player, data) ->
		{
			if(player.containerMenu instanceof MicrochipMenu menu)
			{
				var entry = menu.microchip().stickyNotes().get(data);
				if(entry != null)
				{
					return new MicrochipStickyNoteReference(entry);
				}
			}
			return null;
		});
		
		private final Factory factory;
		
		ReferenceType(Factory factory)
		{
			this.factory = factory;
		}
		
		/**
		 * Creates a sticky note reference for the given type.
		 *
		 * @param player the player
		 * @param data   the data value
		 * @return the sticky note reference, null if no reference could be made
		 */
		public StickyNoteReference create(Player player, int data)
		{
			return factory.create(player, data);
		}
		
		private interface Factory
		{
			StickyNoteReference create(Player player, int data);
		}
	}
	
	public enum Action
	{
		OPEN_VIEW(true),
		OPEN_EDIT(true),
		DONE_EDIT(false);
		
		private final boolean clientbound;
		
		Action(boolean clientbound)
		{
			this.clientbound = clientbound;
		}
		
		public boolean isClientbound()
		{
			return clientbound;
		}
	}
}
