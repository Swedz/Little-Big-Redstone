package net.swedz.little_big_redstone.item.floppydisk;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public record FloppyDiskInstallResult(
		List<ItemStack> present,
		List<ItemStack> missing,
		IItemHandler remainingDrops
) implements Iterable<ItemStack>
{
	public FloppyDiskInstallResult(
			List<ItemStack> present,
			List<ItemStack> missing,
			IItemHandler remainingDrops
	)
	{
		present = Lists.newArrayList(present);
		missing = Lists.newArrayList(missing);
		present.sort(FloppyDiskInstaller.comparator());
		missing.sort(FloppyDiskInstaller.comparator());
		this.present = Collections.unmodifiableList(present);
		this.missing = Collections.unmodifiableList(missing);
		this.remainingDrops = remainingDrops;
	}
	
	public FloppyDiskInstallResult()
	{
		this(List.of(), List.of(), EmptyItemHandler.INSTANCE);
	}
	
	public boolean isSuccess()
	{
		return missing.isEmpty();
	}
	
	public int size()
	{
		return present.size() + missing.size();
	}
	
	@Override
	public Iterator<ItemStack> iterator()
	{
		return Iterators.concat(present.iterator(), missing.iterator());
	}
}
