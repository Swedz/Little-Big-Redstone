package net.swedz.little_big_redstone.gui.stickynote.reference;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.network.packet.StickyNotePacket;

public final class HeldItemStickyNoteReference implements StickyNoteReference
{
	private final InteractionHand hand;
	
	private final DyeColor color, textColor;
	
	private final String text;
	
	private final boolean editable;
	
	public HeldItemStickyNoteReference(InteractionHand hand, ItemStack stack)
	{
		if(!StickyNoteItem.hasRelevantComponents(stack))
		{
			throw new IllegalArgumentException("Non-sticky note item supplied");
		}
		this.hand = hand;
		this.color = stack.get(LBRComponents.STICKY_NOTE_COLOR);
		this.textColor = stack.get(LBRComponents.STICKY_NOTE_TEXT_COLOR);
		this.text = stack.getOrDefault(LBRComponents.STICKY_NOTE, StickyNote.EMPTY).text();
		this.editable = stack.get(LBRComponents.STICKY_NOTE_EDITABLE);
	}
	
	private HeldItemStickyNoteReference(InteractionHand hand, DyeColor color, DyeColor textColor, String text, boolean editable)
	{
		this.hand = hand;
		this.color = color;
		this.textColor = textColor;
		this.text = text;
		this.editable = editable;
	}
	
	@Override
	public DyeColor color()
	{
		return color;
	}
	
	@Override
	public DyeColor textColor()
	{
		return textColor;
	}
	
	@Override
	public String text()
	{
		return text;
	}
	
	@Override
	public boolean canEdit()
	{
		return editable;
	}
	
	@Override
	public StickyNoteReference withText(String text)
	{
		return new HeldItemStickyNoteReference(hand, color, textColor, text, editable);
	}
	
	@Override
	public void saveClient(Level level, Player player)
	{
		new StickyNotePacket(StickyNotePacket.ReferenceType.HELD_ITEM, hand.ordinal(), StickyNotePacket.Action.DONE_EDIT, text).sendToServer();
	}
	
	@Override
	public void saveServer(Level level, Player player)
	{
		var stack = player.getItemInHand(hand);
		if(StickyNoteItem.hasRelevantComponents(stack) &&
		   stack.get(LBRComponents.STICKY_NOTE_EDITABLE))
		{
			stack.set(LBRComponents.STICKY_NOTE, new StickyNote(text));
		}
	}
	
	@Override
	public boolean isStillValid(Level level, Player player)
	{
		return player.getItemInHand(hand).is(LBRTags.Items.STICKY_NOTES);
	}
}
