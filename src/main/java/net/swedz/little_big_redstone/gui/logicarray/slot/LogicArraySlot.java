package net.swedz.little_big_redstone.gui.logicarray.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.function.Supplier;

public final class LogicArraySlot extends SlotItemHandler
{
	private final Supplier<Boolean> isActive;
	
	public LogicArraySlot(IItemHandler itemHandler, int index, int x, int y,
						  Supplier<Boolean> isActive)
	{
		super(itemHandler, index, x, y);
		this.isActive = isActive;
	}
	
	@Override
	public boolean isActive()
	{
		return isActive == null || isActive.get();
	}
	
	@Override
	public ItemStack getItem()
	{
		return super.getItem().copy();
	}
}
