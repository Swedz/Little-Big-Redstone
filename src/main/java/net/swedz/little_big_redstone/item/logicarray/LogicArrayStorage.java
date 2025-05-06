package net.swedz.little_big_redstone.item.logicarray;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.gui.logicarray.LogicArrayMenu;
import net.swedz.tesseract.neoforge.api.Assert;

import java.util.ArrayList;
import java.util.List;

public final class LogicArrayStorage
{
	private static final int MAX_SLOTS = LogicArrayMenu.SLOT_COUNT;
	
	public static final LogicArrayStorage EMPTY = new LogicArrayStorage(NonNullList.create());
	
	public static final Codec<LogicArrayStorage> CODEC = LogicArrayStorage.Slot.CODEC
			.sizeLimitedListOf(MAX_SLOTS)
			.xmap(LogicArrayStorage::fromSlots, LogicArrayStorage::asSlots);
	
	public static final StreamCodec<RegistryFriendlyByteBuf, LogicArrayStorage> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC
			.apply(ByteBufCodecs.list(MAX_SLOTS))
			.map(LogicArrayStorage::new, (storage) -> storage.items);
	
	private static LogicArrayStorage fromSlots(List<Slot> slots)
	{
		var largestIndex = slots.stream().mapToInt(Slot::index).max();
		if(largestIndex.isEmpty())
		{
			return EMPTY;
		}
		LogicArrayStorage storage = new LogicArrayStorage(largestIndex.getAsInt() + 1);
		for(Slot slot : slots)
		{
			storage.items.set(slot.index(), slot.item());
		}
		return storage;
	}
	
	private static int findLastNonEmptySlot(List<ItemStack> items)
	{
		for(int i = items.size() - 1; i >= 0; i--)
		{
			if(!items.get(i).isEmpty())
			{
				return i;
			}
		}
		return -1;
	}
	
	public static LogicArrayStorage fromItems(List<ItemStack> items)
	{
		int lastNonEmptySlot = findLastNonEmptySlot(items);
		if(lastNonEmptySlot == -1)
		{
			return EMPTY;
		}
		LogicArrayStorage storage = new LogicArrayStorage(lastNonEmptySlot + 1);
		for(int i = 0; i <= lastNonEmptySlot; i++)
		{
			storage.items.set(i, items.get(i).copy());
		}
		return storage;
	}
	
	private final NonNullList<ItemStack> items;
	
	private final int itemCount;
	
	private final int hashCode;
	
	@SuppressWarnings("deprecation")
	private LogicArrayStorage(NonNullList<ItemStack> items)
	{
		Assert.that(items.size() <= MAX_SLOTS, "Got %d items, but maximum is %d".formatted(items.size(), MAX_SLOTS));
		
		this.items = items;
		
		int itemCount = 0;
		for(var stack : items)
		{
			itemCount++;
		}
		this.itemCount = itemCount;
		
		this.hashCode = ItemStack.hashStackList(items);
	}
	
	private LogicArrayStorage(int size)
	{
		this(NonNullList.withSize(size, ItemStack.EMPTY));
	}
	
	private LogicArrayStorage(List<ItemStack> items)
	{
		this(items.size());
		
		for(int i = 0; i < items.size(); i++)
		{
			this.items.set(i, items.get(i));
		}
	}
	
	private List<Slot> asSlots()
	{
		List<Slot> list = new ArrayList<>();
		for(int i = 0; i < items.size(); i++)
		{
			ItemStack stack = items.get(i);
			if(!stack.isEmpty())
			{
				list.add(new Slot(i, stack));
			}
		}
		return list;
	}
	
	public void copyInto(NonNullList<ItemStack> list)
	{
		for(int i = 0; i < list.size(); i++)
		{
			var stack = i < this.items.size() ? this.items.get(i) : ItemStack.EMPTY;
			list.set(i, stack.copy());
		}
	}
	
	public int getSlots()
	{
		return items.size();
	}
	
	public boolean isEmpty()
	{
		return items.isEmpty();
	}
	
	public ItemStack getStackInSlot(int slot)
	{
		Assert.that(slot >= 0 && slot < this.getSlots(), "Slot %d not in valid range - [0,%d)".formatted(slot, this.getSlots()));
		return items.get(slot);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicArrayStorage other && ItemStack.listMatches(items, other.items));
	}
	
	@Override
	public int hashCode()
	{
		return hashCode;
	}
	
	private record Slot(int index, ItemStack item)
	{
		public Slot
		{
			Assert.that(item.has(LBRComponents.LOGIC), "Tried to store non-logic item in a logic array storage slot: " + item);
		}
		
		public static final Codec<Slot> CODEC = RecordCodecBuilder.create((instance) -> instance
				.group(
						Codec.intRange(0, MAX_SLOTS).fieldOf("slot").forGetter(Slot::index),
						ItemStack.CODEC.fieldOf("item").forGetter(Slot::item)
				).apply(instance, Slot::new));
	}
}
