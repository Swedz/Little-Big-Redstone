package net.swedz.little_big_redstone.gui.logicarray.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRComponents;

public final class LogicArraySlot extends Slot
{
	public LogicArraySlot(Container container, int slot, int x, int y)
	{
		super(container, slot, x, y);
	}
	
	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return stack.has(LBRComponents.LOGIC);
	}
}
