package net.swedz.little_big_redstone.item.logicarray;

import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemAccessItemHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;

public final class LogicArrayItemHandler extends ItemAccessItemHandler
{
	public LogicArrayItemHandler(ItemAccess access)
	{
		super(access, LBRComponents.LOGIC_ARRAY_STORAGE.get(), LogicArrayItem.MAX_SLOTS);
	}
	
	@Override
	public boolean isValid(int index, ItemResource resource)
	{
		return resource.isEmpty() ||
			   resource.has(LBRComponents.LOGIC_CONFIG) ||
			   resource.is(LBRItems.REDSTONE_BIT.asItem());
	}
	
	public void set(int index, ItemResource newResource, int newAmount)
	{
		// TODO this.update(itemAccess.getResource(), index, newResource, newAmount);
	}
}
