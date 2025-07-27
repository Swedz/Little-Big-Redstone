package net.swedz.little_big_redstone.gui.stickynote.reference;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;
import net.swedz.little_big_redstone.network.packet.StickyNotePacket;

public final class EntityStickyNoteReference implements StickyNoteReference
{
	private final int entityId;
	
	private final DyeColor color, textColor;
	
	private final String text;
	
	public EntityStickyNoteReference(StickyNoteEntity entity)
	{
		this(entity.getId(), entity.getColor(), entity.getTextColor(), entity.getNote().text());
	}
	
	private EntityStickyNoteReference(int entityId, DyeColor color, DyeColor textColor, String text)
	{
		this.entityId = entityId;
		this.color = color;
		this.textColor = textColor;
		this.text = text;
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
	public StickyNoteReference withText(String text)
	{
		return new EntityStickyNoteReference(entityId, color, textColor, text);
	}
	
	@Override
	public void saveClient(Level level, Player player)
	{
		new StickyNotePacket(StickyNotePacket.ReferenceType.ENTITY, entityId, StickyNotePacket.Action.DONE_EDIT, text).sendToServer();
	}
	
	@Override
	public void saveServer(Level level, Player player)
	{
		if(level.getEntity(entityId) instanceof StickyNoteEntity entity &&
		   entity.distanceTo(player) <= 16)
		{
			entity.setNote(new StickyNote(text));
		}
	}
	
	@Override
	public boolean isStillValid(Level level, Player player)
	{
		var entity = level.getEntity(entityId);
		return entity != null && entity.distanceTo(player) <= 16;
	}
}
