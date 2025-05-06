package net.swedz.little_big_redstone.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public abstract class BaseContainerMenu extends AbstractContainerMenu
{
	protected BaseContainerMenu(MenuType<?> menuType, int containerId)
	{
		super(menuType, containerId);
	}
	
	protected void setupPlayerInventory(Inventory playerInventory, int startX, int startY, SlotFactory slotFactory)
	{
		for(int row = 0; row < 3; ++row)
		{
			for(int column = 0; column < 9; ++column)
			{
				this.addSlot(slotFactory.create(playerInventory, column + row * 9 + 9, startX + column * 18, row * 18 + startY));
			}
		}
		for(int column = 0; column < 9; ++column)
		{
			this.addSlot(slotFactory.create(playerInventory, column, startX + column * 18, 58 + startY));
		}
	}
	
	protected void setupPlayerInventory(Inventory playerInventory, int startX, int startY)
	{
		this.setupPlayerInventory(playerInventory, startX, startY, Slot::new);
	}
	
	public interface SlotFactory
	{
		Slot create(Inventory container, int slot, int x, int y);
	}
}
