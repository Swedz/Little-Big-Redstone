package net.swedz.little_big_redstone.gui.noteboard;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRMenus;
import net.swedz.tesseract.neoforge.helper.gui.PlayerInventoryContainerMenu;

public final class NoteBoardMenu extends PlayerInventoryContainerMenu
{
	public NoteBoardMenu(int containerId, Inventory inventory)
	{
		super(LBRMenus.NOTE_BOARD.get(), containerId);
		
		this.setupPlayerInventory(inventory, 8, 8);
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int slot)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return true;
	}
}
