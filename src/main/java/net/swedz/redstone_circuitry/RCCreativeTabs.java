package net.swedz.redstone_circuitry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.Comparator;
import java.util.function.Supplier;

public final class RCCreativeTabs
{
	private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RedstoneCircuitry.ID);
	
	public static final Supplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register(RedstoneCircuitry.ID, () -> CreativeModeTab.builder()
			.title(Component.translatable(RedstoneCircuitry.id(RedstoneCircuitry.ID).toLanguageKey("itemGroup")))
			.icon(() -> RCBlocks.MICROCHIP.get().asItem().getDefaultInstance())
			.displayItems((params, output) ->
			{
				Comparator<ItemHolder> compareBySortOrder = Comparator.comparing(ItemHolder::sortOrder);
				Comparator<ItemHolder> compareByName = Comparator.comparing((i) -> i.identifier().id());
				RCItems.values().stream()
						.sorted(compareBySortOrder.thenComparing(compareByName))
						.forEach(output::accept);
			})
			.build());
	
	public static void init(IEventBus bus)
	{
		CREATIVE_MODE_TABS.register(bus);
	}
}
