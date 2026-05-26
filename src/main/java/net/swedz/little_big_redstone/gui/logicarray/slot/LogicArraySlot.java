package net.swedz.little_big_redstone.gui.logicarray.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

import java.util.function.Supplier;

public final class LogicArraySlot extends ResourceHandlerSlot
{
	private final Supplier<Boolean> isActive;
	
	public LogicArraySlot(
			ResourceHandler<ItemResource> handler,
			int index,
			int x,
			int y,
			Supplier<Boolean> isActive
	)
	{
		super(handler, index, x, y);
		this.isActive = isActive;
	}
	
	@Override
	public boolean isActive()
	{
		return isActive == null || isActive.get();
	}
}
