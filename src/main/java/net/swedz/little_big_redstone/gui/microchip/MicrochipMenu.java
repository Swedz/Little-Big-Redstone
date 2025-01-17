package net.swedz.little_big_redstone.gui.microchip;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRMenus;

public final class MicrochipMenu extends AbstractContainerMenu
{
	public MicrochipMenu(int containerId, Inventory playerInventory)
	{
		super(LBRMenus.MICROCHIP.get(), containerId);
		
		this.setupPlayerInventory(playerInventory, 256 - 90 - 12 - 12);
	}
	
	private void setupPlayerInventory(Inventory playerInventory, int startY)
	{
		for(int row = 0; row < 3; ++row)
		{
			for(int column = 0; column < 9; ++column)
			{
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 48 + column * 18, 8 + row * 18 + startY));
			}
		}
		for(int column = 0; column < 9; ++column)
		{
			this.addSlot(new Slot(playerInventory, column, 48 + column * 18, 66 + startY));
		}
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int slotId)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return true;
	}
}
