package net.swedz.little_big_redstone.gui.logicarray;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRMenus;
import net.swedz.little_big_redstone.gui.BaseContainerMenu;
import net.swedz.little_big_redstone.gui.logicarray.slot.LogicArrayPlayerSlot;
import net.swedz.little_big_redstone.gui.logicarray.slot.LogicArraySlot;

public final class LogicArrayMenu extends BaseContainerMenu
{
	public static final int ROWS       = 4;
	public static final int COLUMNS    = 7;
	public static final int SLOT_COUNT = ROWS * COLUMNS;
	
	private final Container container;
	
	public LogicArrayMenu(int containerId, Inventory playerInventory, Container container)
	{
		super(LBRMenus.LOGIC_ARRAY.get(), containerId);
		
		this.container = container;
		container.startOpen(playerInventory.player);
		
		setupLogicArrayInventory(container, this::addSlot, 26, 18, ROWS, COLUMNS);
		
		this.setupPlayerInventory(playerInventory, 8, 104, LogicArrayPlayerSlot::new);
	}
	
	public LogicArrayMenu(int containerId, Inventory playerInventory)
	{
		this(containerId, playerInventory, new SimpleContainer(SLOT_COUNT));
	}
	
	public static void setupLogicArrayInventory(Container container, SlotAdder slotAdder, int startX, int startY, int rows, int columns)
	{
		for(int row = 0; row < rows; row++)
		{
			for(int column = 0; column < columns; column++)
			{
				slotAdder.addSlot(new LogicArraySlot(container, column + row * columns, startX + column * 18, row * 18 + startY));
			}
		}
	}
	
	public interface SlotAdder
	{
		void addSlot(Slot slot);
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int index)
	{
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if(slot != null && slot.hasItem())
		{
			stack = slot.getItem().copy();
			if(index < SLOT_COUNT)
			{
				if(!this.moveItemStackTo(stack, SLOT_COUNT, slots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if(!this.moveItemStackTo(stack, 0, SLOT_COUNT, false))
			{
				return ItemStack.EMPTY;
			}
			
			if(stack.isEmpty())
			{
				slot.setByPlayer(ItemStack.EMPTY);
			}
			else
			{
				slot.setChanged();
			}
		}
		
		return stack;
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return container.stillValid(player);
	}
	
	@Override
	public void removed(Player player)
	{
		super.removed(player);
		container.stopOpen(player);
	}
}
