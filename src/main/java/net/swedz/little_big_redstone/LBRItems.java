package net.swedz.little_big_redstone;

import com.google.common.collect.Maps;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.client.model.BasicCustomLoaderBuilder;
import net.swedz.little_big_redstone.item.FloppyDiskItem;
import net.swedz.little_big_redstone.item.LogicItem;
import net.swedz.little_big_redstone.item.logicarray.LogicArrayItem;
import net.swedz.little_big_redstone.item.logicarray.LogicArrayItemHandler;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
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
	
	private static final Map<DyeColor, ItemHolder<LogicArrayItem>> LOGIC_ARRAYS;
	private static final Map<DyeColor, ItemHolder<FloppyDiskItem>> FLOPPY_DISKS;
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
		
		Map<DyeColor, ItemHolder<LogicArrayItem>> logicArrays = Maps.newHashMap();
		Map<DyeColor, ItemHolder<FloppyDiskItem>> floppyDisks = Maps.newHashMap();
		Map<DyeColor, ItemHolder<StickyNoteItem>> stickyNotes = Maps.newHashMap();
		LBRColors.forEachIndexed((color, colorName, index) ->
		{
			logicArrays.put(color, createLogicArray(color, colorName, index).register());
			floppyDisks.put(color, createFloppyDisk(color, colorName, index).register());
			stickyNotes.put(color, createStickyNote(color, colorName, index).register());
		});
		LOGIC_ARRAYS = Collections.unmodifiableMap(logicArrays);
		FLOPPY_DISKS = Collections.unmodifiableMap(floppyDisks);
		STICKY_NOTES = Collections.unmodifiableMap(stickyNotes);
	}
	
	public static ItemHolder<LogicArrayItem> logicArray(DyeColor color)
	{
		Assert.notNull(color);
		return LOGIC_ARRAYS.get(color);
	}
	
	public static ItemHolder<FloppyDiskItem> floppyDisk(DyeColor color)
	{
		Assert.notNull(color);
		return FLOPPY_DISKS.get(color);
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
		return create(id, englishName, (p) -> new LogicItem(p, type), LBRSortOrder.LOGIC.and(order))
				.tag(LBRTags.Items.LOGIC_COMPONENTS);
	}
	
	private static ItemHolder<LogicArrayItem> createLogicArray(DyeColor color, String colorEnglishName, int order)
	{
		final String colorId = color.getName();
		final String id = "%s_logic_array".formatted(colorId);
		final String englishName = "%s Logic Array".formatted(colorEnglishName);
		return create(id, englishName, (p) -> new LogicArrayItem(p, color), LBRSortOrder.LOGIC_ARRAYS.and(order))
				.tag(LBRTags.Items.LOGIC_ARRAYS)
				.withCapabilities((item, event) ->
						event.registerItem(Capabilities.ItemHandler.ITEM, (stack, __) -> new LogicArrayItemHandler(stack), item))
				.withModel((holder) -> (provider) ->
						provider.getBuilder(holder.identifier().id())
								.parent(new ModelFile.UncheckedModelFile("item/generated"))
								.texture("layer0", LBR.id("item/logic_array_%s".formatted(colorId))));
	}
	
	private static ItemHolder<FloppyDiskItem> createFloppyDisk(DyeColor color, String colorEnglishName, int order)
	{
		final String colorId = color.getName();
		final String id = "%s_floppy_disk".formatted(colorId);
		final String englishName = "%s Floppy Disk".formatted(colorEnglishName);
		return create(id, englishName, (p) -> new FloppyDiskItem(p, color), LBRSortOrder.FLOPPY_DISKS.and(order))
				.tag(LBRTags.Items.FLOPPY_DISKS)
				.withModel((holder) -> (provider) ->
						provider.getBuilder(holder.identifier().id())
								.parent(new ModelFile.UncheckedModelFile(LBR.id("item/floppy_disk")))
								.texture("layer0", LBR.id("item/floppy_disk_%s".formatted(colorId))));
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
								.parent(new ModelFile.UncheckedModelFile(LBR.id("item/sticky_note")))
								.customLoader((parent, efh) -> new BasicCustomLoaderBuilder<>(LBR.id("sticky_note_item"), parent, efh)).end()
								.texture("layer0", LBR.id("item/sticky_note/%s".formatted(colorId)))
								.texture("layer1", LBR.id("item/sticky_note/text")));
	}
}
