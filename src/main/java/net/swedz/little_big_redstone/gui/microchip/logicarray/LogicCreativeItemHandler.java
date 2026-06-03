package net.swedz.little_big_redstone.gui.microchip.logicarray;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRCreativeTabs;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.item.logicarray.LogicArrayItem;

public final class LogicCreativeItemHandler implements IItemHandlerModifiable
{
	public static final int VISIBLE_COLUMNS = LogicArrayItem.ROWS;
	public static final int VISIBLE_ROWS    = LogicArrayItem.COLUMNS;
	public static final int VISIBLE_SLOTS   = VISIBLE_COLUMNS * VISIBLE_ROWS;

	private int scrollRows;

	public int scrollRows()
	{
		return scrollRows;
	}

	public int maxScrollRows()
	{
		int totalItems = LBRCreativeTabs.getLogicArrayItems().size();
		int totalRows = (totalItems + VISIBLE_COLUMNS - 1) / VISIBLE_COLUMNS;
		return Math.max(0, totalRows - VISIBLE_ROWS);
	}

	public void setScrollRows(int scrollRows)
	{
		this.scrollRows = Math.max(0, Math.min(this.maxScrollRows(), scrollRows));
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack)
	{
	}

	@Override
	public int getSlots()
	{
		return VISIBLE_SLOTS;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		var items = LBRCreativeTabs.getLogicArrayItems();
		int realIndex = slot + scrollRows * VISIBLE_COLUMNS;
		return realIndex >= 0 && realIndex < items.size() ? items.get(realIndex) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		var items = LBRCreativeTabs.getLogicArrayItems();
		int realIndex = slot + scrollRows * VISIBLE_COLUMNS;
		return realIndex >= 0 && realIndex < items.size() ? items.get(realIndex).copyWithCount(amount) : ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack)
	{
		return stack.isEmpty() ||
			   stack.has(LBRComponents.LOGIC_CONFIG) ||
			   stack.is(LBRItems.REDSTONE_BIT.asItem());
	}
}
