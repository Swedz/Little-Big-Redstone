package net.swedz.redstone_circuitry;

import com.google.common.collect.Sets;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.redstone_circuitry.item.LogicItem;
import net.swedz.redstone_circuitry.microchip.logic.LogicType;
import net.swedz.redstone_circuitry.microchip.logic.Logics;
import net.swedz.tesseract.neoforge.registry.SortOrder;
import net.swedz.tesseract.neoforge.registry.common.CommonModelBuilders;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.Set;
import java.util.function.Function;

public final class RCItems
{
	public static final class Registry
	{
		public static final  DeferredRegister.Items ITEMS   = DeferredRegister.createItems(RedstoneCircuitry.ID);
		private static final Set<ItemHolder>        HOLDERS = Sets.newHashSet();
		
		private static void init(IEventBus bus)
		{
			ITEMS.register(bus);
		}
		
		public static void include(ItemHolder holder)
		{
			HOLDERS.add(holder);
		}
	}
	
	public static void init(IEventBus bus)
	{
		Registry.init(bus);
	}
	
	static
	{
		int index = 0;
		for(LogicType<?> gateType : Logics.values())
		{
			createGate(gateType.id(), gateType.englishName(), gateType, index++).register();
		}
	}
	
	public static Set<ItemHolder> values()
	{
		return Set.copyOf(Registry.HOLDERS);
	}
	
	public static <Type extends Item> ItemHolder<Type> create(
			String id, String englishName,
			Function<Item.Properties, Type> creator,
			SortOrder sortOrder
	)
	{
		ItemHolder<Type> holder = new ItemHolder<>(RedstoneCircuitry.id(id), englishName, Registry.ITEMS, creator).sorted(sortOrder);
		Registry.include(holder);
		return holder;
	}
	
	public static ItemHolder<LogicItem> createGate(String id, String englishName, LogicType<?> type, int order)
	{
		return create(id + "_gate", englishName + " Gate", (p) -> new LogicItem(p, type), RCSortOrder.LOGIC_GATES.and(order)).withModelBuilder(CommonModelBuilders::generated);
	}
}
