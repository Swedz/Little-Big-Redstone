package net.swedz.little_big_redstone.item.logicarray;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.swedz.little_big_redstone.LBRComponents;

public final class LogicArrayItemHandler extends ComponentItemHandler
{
	public LogicArrayItemHandler(MutableDataComponentHolder parent)
	{
		super(parent, LBRComponents.LOGIC_ARRAY_STORAGE.get(), LogicArrayItem.MAX_SLOTS);
	}
	
	@Override
	public boolean isItemValid(int slot, ItemStack stack)
	{
		return stack.isEmpty() || stack.has(LBRComponents.LOGIC);
	}
}
