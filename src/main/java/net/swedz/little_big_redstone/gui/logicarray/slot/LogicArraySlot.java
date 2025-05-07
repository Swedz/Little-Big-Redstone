package net.swedz.little_big_redstone.gui.logicarray.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.function.Supplier;

public final class LogicArraySlot extends SlotItemHandler
{
	private final Supplier<Boolean> isActive;
	private final Supplier<Boolean> isCreative;
	
	public LogicArraySlot(IItemHandler itemHandler, int index, int x, int y,
						  Supplier<Boolean> isActive, Supplier<Boolean> isCreative)
	{
		super(itemHandler, index, x, y);
		this.isActive = isActive;
		this.isCreative = isCreative;
	}
	
	@Override
	public boolean isActive()
	{
		return isActive == null || isActive.get();
	}
	
	public boolean isCreative()
	{
		return isCreative != null && isCreative.get();
	}
	
	@Override
	public ItemStack getItem()
	{
		return this.isCreative() ? ItemStack.EMPTY : super.getItem();
	}
	
	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return this.isCreative() || super.mayPlace(stack);
	}
	
	@Override
	public void set(ItemStack stack)
	{
		if(!this.isCreative())
		{
			super.set(stack);
		}
	}
	
	@Override
	public boolean mayPickup(Player playerIn)
	{
		return this.isCreative() || super.mayPickup(playerIn);
	}
	
	@Override
	public ItemStack remove(int amount)
	{
		return this.isCreative() ? ItemStack.EMPTY : super.remove(amount);
	}
}
