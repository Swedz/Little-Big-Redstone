package net.swedz.little_big_redstone.item.floppydisk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.VoidingResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.tesseract.api.tuple.Pair;
import net.swedz.tesseract.neoforge.item.ItemStackInstance;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public final class FloppyDiskInstaller
{
	public static Comparator<ItemStack> comparator()
	{
		return Comparator
				.comparingInt(ItemStack::getCount)
				.thenComparing((stack) -> stack.typeHolder().getKey())
				.reversed();
	}
	
	@SuppressWarnings("deprecation")
	static List<ItemStackInstance> asItems(Microchip.Immutable microchip)
	{
		Map<Pair<Holder<Item>, DataComponentPatch>, Integer> itemCounts = Maps.newHashMap();
		
		if(microchip.wireCount() > 0)
		{
			itemCounts.put(new Pair<>(LBRItems.REDSTONE_BIT.asItem().builtInRegistryHolder(), DataComponentPatch.EMPTY), microchip.wireCount());
		}
		
		for(var object : microchip.objects())
		{
			var stack = object.toStack();
			itemCounts.compute(new Pair<>(stack.typeHolder(), stack.getComponentsPatch()), (__, count) -> count == null ? 1 : (count + 1));
		}
		
		return itemCounts.entrySet().stream()
				.map((entry) -> new ItemStackInstance(entry.getKey().a(), entry.getValue(), entry.getKey().b()))
				.toList();
	}
	
	static List<ItemStack> asItemStacks(Microchip.Immutable microchip)
	{
		return asItems(microchip).stream()
				.flatMap(ItemStackInstance::asStacks)
				.toList();
	}
	
	static ItemStacksResourceHandler asItemHandler(Microchip.Immutable microchip)
	{
		var stacks = asItemStacks(microchip);
		return new ItemStacksResourceHandler(NonNullList.of(ItemStack.EMPTY, stacks.toArray(ItemStack[]::new)));
	}
	
	private record Context(
			Inventory playerInventory,
			ResourceHandler<ItemResource> targetInventory,
			Microchip.Immutable source,
			List<ItemStack> presentItems,
			List<ItemStack> missingItems
	)
	{
		public ResourceHandler<ItemResource> handler()
		{
			return new CombinedResourceHandler<>(
					PlayerInventoryWrapper.of(playerInventory),
					targetInventory
			);
		}
	}
	
	private static void consumeItemsWires(Context context, Transaction transaction)
	{
		int totalWiresNeeded = context.source().wireCount();
		int wiresAvailable = ResourceHandlerUtil.move(
				context.handler(),
				new VoidingResourceHandler<>(ItemResource.EMPTY),
				(resource) -> resource.is(LBRItems.REDSTONE_BIT),
				totalWiresNeeded,
				transaction
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
			Function<ItemResource, Optional<DyeColor>> colorGetter,
			Consumer<ItemStack> stackApplier
	)
	{
		public Optional<DyeColor> colorOf(ItemResource resource)
		{
			return colorGetter.apply(resource);
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
							Optional.ofNullable(stack.get(LBRComponents.LOGIC_COLOR)),
					(stack) ->
					{
						stack.set(LBRComponents.LOGIC_CONFIG, entry.component().config());
						stack.set(LBRComponents.LOGIC_COLOR, (DyeColor) entry.component().color().orElse(null));
					}
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
			Map<NeededItem, Integer> neededItems,
			boolean exactMatchesOnly,
			Transaction transaction
	)
	{
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
				var dyeColor = item.color().get();
				int dyeExtracted = ResourceHandlerUtil.move(
						context.handler(),
						new VoidingResourceHandler<>(ItemResource.EMPTY),
						(resource) ->
								resource.has(DataComponents.DYE) &&
								resource.get(DataComponents.DYE).equals(dyeColor),
						count,
						transaction
				);
				if(dyeExtracted <= 0)
				{
					continue;
				}
				amountToExtract = dyeExtracted;
			}
			
			int extracted = ResourceHandlerUtil.move(
					context.handler(),
					new VoidingResourceHandler<>(ItemResource.EMPTY),
					(resource) ->
					{
						if(resource.is(item.item()))
						{
							if(exactMatchesOnly)
							{
								var color = item.colorOf(resource);
								return Objects.equals(color, item.color());
							}
							return true;
						}
						return false;
					},
					amountToExtract,
					transaction
			);
			
			entry.setValue(count - extracted);
		}
	}
	
	private static void gatherPresentAndMissingItems(
			Context context,
			Map<NeededItem, Integer> neededItems,
			Map<NeededItem, Integer> neededItemsOriginal
	)
	{
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
		
		var targetContents = asItemHandler(target);
		var context = new Context(
				player.getInventory(),
				targetContents,
				source,
				presentItems,
				missingItems
		);
		
		NonNullList<ItemStack> remainingDrops;
		try(var transaction = Transaction.openRoot())
		{
			consumeItemsWires(context, transaction);
			
			var neededItems = gatherNeededItems(source);
			var neededItemsOriginal = Map.copyOf(neededItems);
			consumeItemsObjects(context, neededItems, true, transaction);
			consumeItemsObjects(context, neededItems, false, transaction);
			gatherPresentAndMissingItems(context, neededItems, neededItemsOriginal);
			
			remainingDrops = targetContents.copyToList();
			
			if(!simulate)
			{
				transaction.commit();
			}
		}
		
		return new FloppyDiskInstallResult(presentItems, missingItems, remainingDrops);
	}
}
