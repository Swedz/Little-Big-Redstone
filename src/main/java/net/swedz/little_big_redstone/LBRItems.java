package net.swedz.little_big_redstone;

import com.google.common.collect.Maps;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.item.LogicItem;
import net.swedz.little_big_redstone.item.StickyNoteItem;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;
import net.swedz.tesseract.neoforge.api.Assert;
import net.swedz.tesseract.neoforge.registry.SortOrder;
import net.swedz.tesseract.neoforge.registry.common.CommonModelBuilders;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.Collections;
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
	
	public static final ItemHolder<Item> REDSTONE_BIT = create("redstone_bit", "Redstone Bit", Item::new, LBRSortOrder.RESOURCES).withModelBuilder(CommonModelBuilders::generated).register();
	
	private static final Map<DyeColor, ItemHolder<StickyNoteItem>> STICKY_NOTES;
	
	static
	{
		{
			int index = 0;
			for(LogicType<?> type : LogicTypes.values())
			{
				createLogic(type.id(), type.englishName(), type, index++).register();
			}
		}
		
		Map<DyeColor, ItemHolder<StickyNoteItem>> stickyNotes = Maps.newHashMap();
		LBRColors.forEachIndexed((color, colorName, index) ->
				stickyNotes.put(color, createStickyNote(color, colorName, index).register()));
		STICKY_NOTES = Collections.unmodifiableMap(stickyNotes);
	}
	
	public static ItemHolder<StickyNoteItem> stickyNote(DyeColor color)
	{
		Assert.notNull(color);
		return STICKY_NOTES.get(color);
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
	
	private static ItemHolder<StickyNoteItem> createStickyNote(DyeColor color, String colorEnglishName, int order)
	{
		final String colorId = color.getName();
		final String id = "%s_sticky_note".formatted(colorId);
		final String englishName = "%s Sticky Note".formatted(colorEnglishName);
		return create(id, englishName, (p) -> new StickyNoteItem(p, color), LBRSortOrder.STICKY_NOTES.and(order))
				.tag(LBRTags.Items.STICKY_NOTES)
				.withModel((holder) -> (provider) ->
						provider.getBuilder(holder.identifier().id())
								.parent(new ModelFile.UncheckedModelFile("item/generated"))
								.texture("layer0", LBR.id("item/sticky_note_%s".formatted(colorId))));
	}
}
