package net.swedz.little_big_redstone.gui.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public final class MaybeLockedPlayerSlot extends Slot
{
	private final Predicate<ItemStack> isLocked;
	
	public MaybeLockedPlayerSlot(
			Container container,
			int slot,
			int x,
			int y,
			Predicate<ItemStack> isLocked
	)
	{
		super(container, slot, x, y);
		this.isLocked = isLocked;
	}
	
	public MaybeLockedPlayerSlot(
			Container container,
			int slot,
			int x,
			int y
	)
	{
		this(container, slot, x, y, null);
	}
	
	public boolean isLocked()
	{
		return isLocked != null && isLocked.test(this.getItem());
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
