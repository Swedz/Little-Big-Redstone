package net.swedz.little_big_redstone.item.logicarray;

import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemAccessItemHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRTags;

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
			   resource.is(LBRTags.Items.LOGIC_ARRAY_CONTAINS);
	}
	
	public void set(int index, ItemResource newResource, int newAmount)
	{
		var result = this.update(itemAccess.getResource(), index, newResource, newAmount);
		itemAccess.exchange(result, itemAccess.getAmount(), null);
	}
}
