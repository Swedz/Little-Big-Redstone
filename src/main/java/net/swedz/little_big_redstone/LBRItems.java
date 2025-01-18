package net.swedz.little_big_redstone;

import com.google.common.collect.Maps;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.item.LogicItem;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;
import net.swedz.tesseract.neoforge.registry.SortOrder;
import net.swedz.tesseract.neoforge.registry.common.CommonModelBuilders;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class LBRItems
{
	public static final class Registry
	{
		public static final  DeferredRegister.Items  ITEMS   = DeferredRegister.createItems(LBR.ID);
		private static final Map<String, ItemHolder> HOLDERS = Maps.newHashMap();
		
		private static void init(IEventBus bus)
		{
			ITEMS.register(bus);
		}
		
		public static void include(ItemHolder holder)
		{
			HOLDERS.put(holder.identifier().id(), holder);
		}
	}
	
	public static void init(IEventBus bus)
	{
		Registry.init(bus);
	}
	
	static
	{
		int index = 0;
		for(LogicType<?> type : LogicTypes.values())
		{
			createLogic(type.id(), type.englishName(), type, index++).register();
		}
	}
	
	public static Set<ItemHolder> values()
	{
		return Set.copyOf(Registry.HOLDERS.values());
	}
	
	public static ItemHolder valueOf(String id)
	{
		return Registry.HOLDERS.get(id);
	}
	
	private static <Type extends Item> ItemHolder<Type> create(
			String id, String englishName,
			Function<Item.Properties, Type> creator,
			SortOrder sortOrder
	)
	{
		ItemHolder<Type> holder = new ItemHolder<>(LBR.id(id), englishName, Registry.ITEMS, creator).sorted(sortOrder);
		Registry.include(holder);
		return holder;
	}
	
	private static ItemHolder<LogicItem> createLogic(String id, String englishName, LogicType<?> type, int order)
	{
		return create(id, englishName, (p) -> new LogicItem(p, type), LBRSortOrder.LOGIC.and(order)).withModelBuilder(CommonModelBuilders::generated);
	}
}
