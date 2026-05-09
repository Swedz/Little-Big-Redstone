package net.swedz.little_big_redstone.gui.logicconfig.reference;

import net.minecraft.world.entity.player.Player;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;

public interface LogicConfigReference
{
	/**
	 * Saves the logic config on the server.
	 */
	void save(Player player, LogicComponent component);
	
	/**
	 * Closes the menu for the player with no changes being saved on the server.
	 */
	void cancel(Player player);
	
	/**
	 * Checks if the screen should remain open for the player.
	 *
	 * @param player the player
	 * @return true if the screen should remain open, false otherwise
	 */
	boolean isStillValid(Player player);
}
