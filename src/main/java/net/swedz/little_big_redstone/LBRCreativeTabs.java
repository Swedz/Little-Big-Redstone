package net.swedz.little_big_redstone;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public final class LBRCreativeTabs
{
	private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LBR.ID);
	
	public static final Supplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register(LBR.ID, () -> CreativeModeTab.builder()
			.title(Component.translatable(LBR.id(LBR.ID).toLanguageKey("itemGroup")))
			.icon(() -> LBRBlocks.microchip(DyeColor.RED).get().asItem().getDefaultInstance())
			.displayItems((params, output) ->
			{
				Comparator<ItemHolder> compareBySortOrder = Comparator.comparing(ItemHolder::sortOrder);
				Comparator<ItemHolder> compareByName = Comparator.comparing((i) -> i.identifier().id());
				LBRItems.values().stream()
						.sorted(compareBySortOrder.thenComparing(compareByName))
						.forEach(output::accept);
			})
			.build());
	
	private static final Supplier<List<ItemStack>> LOGIC_ARRAY_ITEMS = Suppliers.memoize(() ->
	{
		List<ItemStack> items = Lists.newArrayList();
		items.add(LBRItems.REDSTONE_BIT.asItem().getDefaultInstance());
		for(LogicType type : LogicTypes.values())
		{
			var stack = type.toStack(type.defaultFactory().create());
			items.add(stack);
		}
		return Collections.unmodifiableList(items);
	});
	
	public static List<ItemStack> getLogicArrayItems()
	{
		return LOGIC_ARRAY_ITEMS.get();
	}
	
	public static void init(IEventBus bus)
	{
		CREATIVE_MODE_TABS.register(bus);
	}
}
