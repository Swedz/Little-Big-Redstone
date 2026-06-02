package net.swedz.little_big_redstone.gui.microchip.logicarray;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRCreativeTabs;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.item.logicarray.LogicArrayItem;

public final class LogicCreativeItemHandler implements ResourceHandler<ItemResource>
{
	@Override
	public int size()
	{
		return LogicArrayItem.MAX_SLOTS;
	}
	
	@Override
	public ItemResource getResource(int index)
	{
		var items = LBRCreativeTabs.getLogicArrayItems();
		return index < items.size() ? items.get(index) : ItemResource.EMPTY;
	}
	
	@Override
	public long getAmountAsLong(int index)
	{
		var items = LBRCreativeTabs.getLogicArrayItems();
		return index < items.size() ? 1 : 0;
	}
	
	@Override
	public long getCapacityAsLong(int index, ItemResource resource)
	{
		return resource.getMaxStackSize();
	}
	
	@Override
	public boolean isValid(int index, ItemResource resource)
	{
		return resource.isEmpty() ||
			   resource.has(LBRComponents.LOGIC_CONFIG) ||
			   resource.is(LBRItems.REDSTONE_BIT.asItem());
	}
	
	@Override
	public int insert(int index, ItemResource resource, int amount, TransactionContext transaction)
	{
		return amount;
	}
	
	@Override
	public int extract(int index, ItemResource resource, int amount, TransactionContext transaction)
	{
		var items = LBRCreativeTabs.getLogicArrayItems();
		return index < items.size() ? amount : 0;
	}
}
