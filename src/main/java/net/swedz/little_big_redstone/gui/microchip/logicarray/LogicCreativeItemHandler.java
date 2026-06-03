package net.swedz.little_big_redstone.gui.microchip.logicarray;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRCreativeTabs;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.item.logicarray.LogicArrayItem;

public final class LogicCreativeItemHandler implements IItemHandlerModifiable
{
	@Override
	public void setStackInSlot(int slot, ItemStack stack)
	{
	}
	
	@Override
	public int getSlots()
	{
		return LogicArrayItem.MAX_SLOTS;
	}
	
	@Override
	public ItemStack getStackInSlot(int slot)
	{
		var items = LBRCreativeTabs.getLogicArrayItems();
		return slot < items.size() ? items.get(slot) : ItemStack.EMPTY;
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
		return slot < items.size() ? items.get(slot).copyWithCount(amount) : ItemStack.EMPTY;
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
