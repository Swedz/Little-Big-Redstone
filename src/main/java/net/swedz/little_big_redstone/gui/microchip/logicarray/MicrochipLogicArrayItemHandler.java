package net.swedz.little_big_redstone.gui.microchip.logicarray;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.item.logicarray.LogicArrayItemHandler;

import java.util.Optional;

public final class MicrochipLogicArrayItemHandler implements ResourceHandler<ItemResource>
{
	private static final LogicCreativeItemHandler CREATIVE = new LogicCreativeItemHandler();
	
	private final AbstractContainerMenu menu;
	private final Player                player;
	
	private int                                     logicArraySlot    = -1;
	private Optional<ResourceHandler<ItemResource>> logicArrayHandler       = Optional.empty();
	private Optional<IndexModifier<ItemResource>>   logicArrayIndexModifier = Optional.empty();
	
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
		return this.handler().isPresent();
	}
	
	private Optional<ResourceHandler<ItemResource>> handler()
	{
		return logicArrayHandler.isEmpty() && player.hasInfiniteMaterials() ? Optional.of(CREATIVE) : logicArrayHandler;
	}
	
	private boolean setPickedLogicArray(int slotId, ItemStack stack)
	{
		if(stack.has(LBRComponents.LOGIC_ARRAY_STORAGE) &&
		   ItemAccess.forStack(stack).getCapability(Capabilities.Item.ITEM) instanceof LogicArrayItemHandler logicArrayItemHandler)
		{
			logicArraySlot = slotId;
			logicArrayHandler = Optional.of(logicArrayItemHandler);
			logicArrayIndexModifier = Optional.of(logicArrayItemHandler::set);
			return true;
		}
		return false;
	}
	
	public void deselectPickedLogicArray()
	{
		logicArraySlot = -1;
		logicArrayHandler = Optional.empty();
		logicArrayIndexModifier = Optional.empty();
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
	public int size()
	{
		return this.handler().map(ResourceHandler::size).orElse(0);
	}
	
	@Override
	public ItemResource getResource(int index)
	{
		return this.handler().map((h) -> h.getResource(index)).orElse(ItemResource.EMPTY);
	}
	
	@Override
	public long getAmountAsLong(int index)
	{
		return this.handler().map((h) -> h.getAmountAsLong(index)).orElse(0L);
	}
	
	@Override
	public long getCapacityAsLong(int index, ItemResource resource)
	{
		return this.handler().map((h) -> h.getCapacityAsLong(index, resource)).orElse(0L);
	}
	
	@Override
	public boolean isValid(int index, ItemResource resource)
	{
		return this.handler().map((h) -> h.isValid(index, resource)).orElse(false);
	}
	
	@Override
	public int insert(int index, ItemResource resource, int amount, TransactionContext transaction)
	{
		return this.handler().map((h) -> h.insert(index, resource, amount, transaction)).orElse(0);
	}
	
	@Override
	public int extract(int index, ItemResource resource, int amount, TransactionContext transaction)
	{
		return this.handler().map((h) -> h.extract(index, resource, amount, transaction)).orElse(0);
	}
	
	public void set(int index, ItemResource newResource, int newAmount)
	{
		logicArrayIndexModifier.ifPresent((im) -> im.set(index, newResource, newAmount));
	}
}
