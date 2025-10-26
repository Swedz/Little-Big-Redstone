package net.swedz.little_big_redstone.entity.stickynote;

/**
 * Injected into {@link net.minecraft.world.entity.player.Player}
 */
public interface StickyNoteWatcher
{
	default Integer getWatchedStickyNote()
	{
		throw new UnsupportedOperationException("getWatchedStickyNote() must be implemented");
	}
	
	default void setWatchedStickyNote(Integer entityId)
	{
		throw new UnsupportedOperationException("setWatchedStickyNote() must be implemented");
	}
}
