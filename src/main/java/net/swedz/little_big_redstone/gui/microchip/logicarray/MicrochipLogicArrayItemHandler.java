package net.swedz.little_big_redstone.gui.microchip.logicarray;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.item.logicarray.LogicArrayItemHandler;

import java.util.Optional;

public final class MicrochipLogicArrayItemHandler implements IItemHandlerModifiable
{
	private final AbstractContainerMenu menu;
	private final Player                player;
	
	private int                              logicArraySlot = -1;
	private Optional<IItemHandlerModifiable> logicArray     = Optional.empty();
	
	public MicrochipLogicArrayItemHandler(AbstractContainerMenu menu, Player player)
	{
		this.menu = menu;
		this.player = player;
	}
	
	public int getSelectedSlot()
	{
		return logicArraySlot;
	}
	
	public boolean hasSelectedSlot()
	{
		return logicArraySlot != -1;
	}
	
	public boolean isCreativeMode()
	{
		return !this.hasSelectedSlot() && player.hasInfiniteMaterials();
	}
	
	public boolean shouldDisplay()
	{
		return this.hasSelectedSlot() || player.hasInfiniteMaterials();
	}
	
	private boolean setPickedLogicArray(int slotId, ItemStack stack)
	{
		if(stack.has(LBRComponents.LOGIC_ARRAY_STORAGE) &&
		   stack.getCapability(Capabilities.ItemHandler.ITEM) instanceof LogicArrayItemHandler logicArrayItemHandler)
		{
			logicArraySlot = slotId;
			logicArray = Optional.of(logicArrayItemHandler);
			return true;
		}
		return false;
	}
	
	public void deselectPickedLogicArray()
	{
		logicArraySlot = -1;
		logicArray = Optional.empty();
	}
	
	public void setPickedLogicArray(int slotId)
	{
		if(slotId < 0 || slotId >= menu.slots.size())
		{
			return;
		}
		
		var stack = menu.slots.get(slotId).getItem();
		this.setPickedLogicArray(slotId, stack);
	}
	
	public void pickLogicArrayFromInventory()
	{
		if(this.isCreativeMode())
		{
			return;
		}
		for(int i = menu.slots.size() - 1; i >= 0; i--)
		{
			var stack = menu.slots.get(i).getItem();
			if(this.setPickedLogicArray(i, stack))
			{
				return;
			}
		}
		this.deselectPickedLogicArray();
	}
	
	@Override
	public void setStackInSlot(int slot, ItemStack stack)
	{
		logicArray.ifPresent((h) -> h.setStackInSlot(slot, stack));
	}
	
	@Override
	public int getSlots()
	{
		return logicArray.map(IItemHandlerModifiable::getSlots).orElse(0);
	}
	
	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return logicArray.map((h) -> h.getStackInSlot(slot)).orElse(ItemStack.EMPTY);
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		return logicArray.map((h) -> h.insertItem(slot, stack, simulate)).orElse(ItemStack.EMPTY);
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return logicArray.map((h) -> h.extractItem(slot, amount, simulate)).orElse(ItemStack.EMPTY);
	}
	
	@Override
	public int getSlotLimit(int slot)
	{
		return logicArray.map((h) -> h.getSlotLimit(slot)).orElse(0);
	}
	
	@Override
	public boolean isItemValid(int slot, ItemStack stack)
	{
		return logicArray.map((h) -> h.isItemValid(slot, stack)).orElse(false);
	}
}
