package net.swedz.little_big_redstone.gui.logicarray.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRItems;

public final class LogicArrayPlayerSlot extends Slot
{
	public LogicArrayPlayerSlot(Container container, int slot, int x, int y)
	{
		super(container, slot, x, y);
	}
	
	public boolean containsLogicArray()
	{
		return this.getItem().is(LBRItems.LOGIC_ARRAY.get());
	}
	
	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return !this.containsLogicArray();
	}
	
	@Override
	public boolean mayPickup(Player player)
	{
		return !this.containsLogicArray();
	}
	
	@Override
	public boolean allowModification(Player player)
	{
		return !this.containsLogicArray();
	}
}
