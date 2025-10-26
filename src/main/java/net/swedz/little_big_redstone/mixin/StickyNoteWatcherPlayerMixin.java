package net.swedz.little_big_redstone.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteWatcher;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
@Implements(@Interface(iface = StickyNoteWatcher.class, prefix = "stickyNote$"))
public abstract class StickyNoteWatcherPlayerMixin extends LivingEntity
{
	protected StickyNoteWatcherPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level)
	{
		super(entityType, level);
	}
	
	@Unique
	private Integer watchedStickyNote;
	
	public Integer stickyNote$getWatchedStickyNote()
	{
		return watchedStickyNote;
	}
	
	public void stickyNote$setWatchedStickyNote(Integer entityId)
	{
		watchedStickyNote = entityId;
	}
}
