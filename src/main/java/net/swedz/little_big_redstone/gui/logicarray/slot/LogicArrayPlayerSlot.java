package net.swedz.little_big_redstone.gui.logicarray.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRItems;

import java.util.function.Supplier;

public final class LogicArrayPlayerSlot extends Slot
{
	private final Supplier<Boolean> isLocked;
	
	public LogicArrayPlayerSlot(Container container, int slot, int x, int y,
								Supplier<Boolean> isLocked)
	{
		super(container, slot, x, y);
		this.isLocked = isLocked;
	}
	
	public LogicArrayPlayerSlot(Container container, int slot, int x, int y)
	{
		this(container, slot, x, y, null);
	}
	
	public boolean isLocked()
	{
		return isLocked != null ? isLocked.get() : this.containsLogicArray();
	}
	
	public boolean containsLogicArray()
	{
		return this.getItem().is(LBRItems.LOGIC_ARRAY.get());
	}
	
	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return !this.isLocked();
	}
	
	@Override
	public boolean mayPickup(Player player)
	{
		return !this.isLocked();
	}
	
	@Override
	public boolean allowModification(Player player)
	{
		return !this.isLocked();
	}
}
