package net.swedz.little_big_redstone.gui.stickynote.reference;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;

public interface StickyNoteReference
{
	DyeColor color();
	
	DyeColor textColor();
	
	String text();
	
	/**
	 * Creates a new instance of the reference with a modified text value.
	 *
	 * @param text the new text content
	 * @return the new reference
	 */
	StickyNoteReference withText(String text);
	
	/**
	 * Sends the packet from the client to the server telling it about the changes made to the sticky note.
	 */
	void saveClient(Level level, Player player);
	
	/**
	 * Updates the sticky note contents on the server side. This should only be called if the data has been validated.
	 *
	 * @param text the new text
	 */
	void saveServer(Level level, Player player);
	
	/**
	 * Checks if the screen should remain open for the player.
	 *
	 * @param level  the level
	 * @param player the player
	 * @return true if the screen should remain open, false otherwise
	 */
	boolean isStillValid(Level level, Player player);
}
