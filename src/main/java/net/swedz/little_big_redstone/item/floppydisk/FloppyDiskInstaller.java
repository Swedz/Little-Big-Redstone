package net.swedz.little_big_redstone.item.floppydisk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.tesseract.neoforge.api.tuple.Pair;
import net.swedz.tesseract.neoforge.helper.TransferHelper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class FloppyDiskInstaller
{
	public static Comparator<ItemStack> comparator()
	{
		return Comparator
				.comparingInt(ItemStack::getCount)
				.thenComparing((stack) -> stack.getItemHolder().getKey())
				.reversed();
	}
	
	public record ItemWithCount(
			Item item,
			DataComponentMap data,
			int count
	)
	{
		public Stream<ItemStack> stacks()
		{
			return toStacks(item, data, count);
		}
	}
	
	private static Stream<ItemStack> toStacks(Item item, DataComponentMap data, int count)
	{
		List<ItemStack> stacks = Lists.newArrayList();
		int remaining = count;
		while(remaining > 0)
		{
			int stackCount = Math.min(64, remaining);
			var stack = new ItemStack(item, stackCount);
			if(!data.isEmpty())
			{
				stack.applyComponents(data);
			}
			stacks.add(stack);
			remaining -= stackCount;
		}
		return stacks.stream();
	}
	
	static List<ItemWithCount> asItems(Microchip.Immutable microchip)
	{
		Map<Pair<Item, DataComponentMap>, Integer> itemCounts = Maps.newHashMap();
		
		if(microchip.wireCount() > 0)
		{
			itemCounts.put(new Pair<>(LBRItems.REDSTONE_BIT.asItem(), DataComponentMap.EMPTY), microchip.wireCount());
		}
		
		for(var object : microchip.objects())
		{
			var stack = object.toStack();
			itemCounts.compute(new Pair<>(stack.getItem(), stack.getComponents()), (__, count) -> count == null ? 1 : (count + 1));
		}
		
		return itemCounts.entrySet().stream()
				.map((entry) -> new ItemWithCount(entry.getKey().a(), entry.getKey().b(), entry.getValue()))
				.toList();
	}
	
	static List<ItemStack> asItemStacks(Microchip.Immutable microchip)
	{
		return asItems(microchip).stream()
				.flatMap(ItemWithCount::stacks)
				.toList();
	}
	
	static IItemHandler asItemHandler(Microchip.Immutable microchip)
	{
		var stacks = asItemStacks(microchip);
		return new ItemStackHandler(NonNullList.of(ItemStack.EMPTY, stacks.toArray(ItemStack[]::new)));
	}
	
	private record Context(
			Inventory playerInventory,
			IItemHandler targetInventory,
			Microchip.Immutable source,
			Microchip.Immutable target,
			boolean simulate,
			List<ItemStack> presentItems,
			List<ItemStack> missingItems
	)
	{
	}
	
	private static int extractAny(
			Context context,
			Predicate<ItemStack> test,
			int maxAmount
	)
	{
		int toExtract = maxAmount;
		int amountExtracted = TransferHelper.extractAny(
				context.targetInventory(),
				test,
				toExtract,
				context.simulate()
		);
		toExtract -= amountExtracted;
		if(toExtract > 0)
		{
			amountExtracted += TransferHelper.extractAny(
					context.playerInventory(),
					test,
					toExtract,
					true,
					context.simulate()
			);
		}
		return amountExtracted;
	}
	
	private static void consumeItemsWires(Context context)
	{
		int totalWiresNeeded = context.source().wireCount();
		int wiresAvailable = extractAny(
				context,
				(item) -> item.is(LBRItems.REDSTONE_BIT.get()),
				totalWiresNeeded
		);
		if(wiresAvailable != totalWiresNeeded)
		{
			context.missingItems().add(new ItemStack(LBRItems.REDSTONE_BIT.get(), totalWiresNeeded - wiresAvailable));
		}
		if(wiresAvailable > 0)
		{
			context.presentItems().add(new ItemStack(LBRItems.REDSTONE_BIT.get(), wiresAvailable));
		}
	}
	
	private record NeededItem(
			Item item,
			Optional<DyeColor> color,
			Function<ItemStack, Optional<DyeColor>> colorGetter,
			Consumer<ItemStack> stackApplier
	)
	{
		public Optional<DyeColor> colorOf(ItemStack stack)
		{
			return colorGetter.apply(stack);
		}
		
		public ItemStack toStack()
		{
			var stack = new ItemStack(item);
			stackApplier.accept(stack);
			return stack;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if(o == null || getClass() != o.getClass())
			{
				return false;
			}
			var other = (NeededItem) o;
			return Objects.equals(item, other.item) &&
				   Objects.equals(color, other.color);
		}
		
		@Override
		public int hashCode()
		{
			return Objects.hash(item, color);
		}
	}
	
	private static Map<NeededItem, Integer> gatherNeededItems(Microchip.Immutable microchip)
	{
		Map<NeededItem, Integer> items = Maps.newHashMap();
		
		for(var entry : microchip.components())
		{
			var item = new NeededItem(
					entry.component().type().item(),
					entry.color(),
					(stack) ->
							stack.has(LBRComponents.LOGIC) ?
									stack.get(LBRComponents.LOGIC).color() :
									Optional.empty(),
					(stack) ->
							stack.set(LBRComponents.LOGIC, entry.component())
			);
			items.compute(item, (__, count) -> count == null ? 1 : (count + 1));
		}
		
		for(var entry : microchip.stickyNotes())
		{
			var item = new NeededItem(
					LBRItems.stickyNote(entry.noteColor()).asItem(),
					entry.color(),
					(stack) ->
							stack.has(LBRComponents.STICKY_NOTE_TEXT_COLOR) ?
									Optional.of(stack.get(LBRComponents.STICKY_NOTE_TEXT_COLOR)) :
									Optional.empty(),
					(stack) ->
					{
						stack.set(LBRComponents.STICKY_NOTE, entry.note());
						stack.set(LBRComponents.STICKY_NOTE_TEXT_COLOR, entry.textColor());
					}
			);
			items.compute(item, (__, count) -> count == null ? 1 : (count + 1));
		}
		
		return items;
	}
	
	private static void consumeItemsObjects(
			Context context,
			Map<NeededItem, Integer> neededItems
	)
	{
		var neededItemsOriginal = Map.copyOf(neededItems);
		
		for(int index = 0; index < 2; index++)
		{
			boolean exactMatchesOnly = index == 0;
			
			for(var entry : neededItems.entrySet())
			{
				var item = entry.getKey();
				var count = entry.getValue();
				
				if(count <= 0 ||
				   // No color is needed, so we can skip the first check
				   (item.color().isEmpty() && exactMatchesOnly))
				{
					continue;
				}
				
				int amountToExtract = count;
				if(!exactMatchesOnly &&
				   item.color().isPresent())
				{
					var dyeItem = DyeItem.byColor(item.color().get());
					int dyeExtracted = extractAny(
							context,
							(stack) -> stack.is(dyeItem),
							count
					);
					if(dyeExtracted <= 0)
					{
						continue;
					}
					amountToExtract = dyeExtracted;
				}
				
				int extracted = extractAny(
						context,
						(stack) ->
						{
							if(stack.is(item.item()))
							{
								if(exactMatchesOnly)
								{
									var color = item.colorOf(stack);
									return Objects.equals(color, item.color());
								}
								return true;
							}
							return false;
						},
						amountToExtract
				);
				
				entry.setValue(count - extracted);
			}
		}
		
		for(var entry : neededItems.entrySet())
		{
			var item = entry.getKey();
			int remainingCount = entry.getValue();
			int originalCount = neededItemsOriginal.get(item);
			
			var stack = item.toStack();
			
			if(originalCount != remainingCount)
			{
				context.presentItems().add(stack.copyWithCount(originalCount - remainingCount));
			}
			if(remainingCount > 0)
			{
				context.missingItems().add(stack.copyWithCount(remainingCount));
			}
		}
	}
	
	public static FloppyDiskInstallResult consumeItems(
			Player player,
			Microchip.Immutable source,
			Microchip.Immutable target,
			boolean simulate
	)
	{
		if(player.hasInfiniteMaterials())
		{
			return new FloppyDiskInstallResult();
		}
		
		List<ItemStack> presentItems = Lists.newArrayList();
		List<ItemStack> missingItems = Lists.newArrayList();
		
		var neededItems = gatherNeededItems(source);
		var targetContents = asItemHandler(target);
		var context = new Context(
				player.getInventory(),
				targetContents,
				source,
				target,
				simulate,
				presentItems,
				missingItems
		);
		
		consumeItemsWires(context);
		consumeItemsObjects(context, neededItems);
		
		return new FloppyDiskInstallResult(presentItems, missingItems, targetContents);
	}
}
