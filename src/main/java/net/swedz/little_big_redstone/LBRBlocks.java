package net.swedz.little_big_redstone;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlock;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.tesseract.api.Assert;
import net.swedz.tesseract.neoforge.helper.model.BasicCustomLoaderBuilder;
import net.swedz.tesseract.neoforge.registry.SortOrder;
import net.swedz.tesseract.neoforge.registry.common.CommonLootTableBuilders;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;
import net.swedz.tesseract.neoforge.registry.holder.BlockWithItemHolder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class LBRBlocks
{
	public static final class Registry
	{
		public static final  DeferredRegister.Blocks              BLOCKS         = DeferredRegister.createBlocks(LBR.ID);
		public static final  DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, LBR.ID);
		private static final Set<BlockHolder>                     HOLDERS        = Sets.newHashSet();
		
		private static void init(IEventBus bus)
		{
			BLOCKS.register(bus);
			BLOCK_ENTITIES.register(bus);
		}
		
		public static void include(BlockHolder holder)
		{
			HOLDERS.add(holder);
		}
	}
	
	public static void init(IEventBus bus)
	{
		Registry.init(bus);
	}
	
	private static final Map<DyeColor, BlockHolder<MicrochipBlock>> MICROCHIPS;
	
	static
	{
		Map<DyeColor, BlockHolder<MicrochipBlock>> microchips = Maps.newHashMap();
		LBRColors.forEachIndexed((color, colorName, index) ->
				microchips.put(color, createMicrochip(color, colorName, index).register()));
		MICROCHIPS = Collections.unmodifiableMap(microchips);
	}
	
	public static BlockHolder<MicrochipBlock> microchip(DyeColor color)
	{
		Assert.notNull(color);
		return MICROCHIPS.get(color);
	}
	
	public static final Supplier<BlockEntityType<MicrochipBlockEntity>> MICROCHIP_ENTITY = Registry.BLOCK_ENTITIES.register(
			"microchip",
			() -> BlockEntityType.Builder.of(
					MicrochipBlockEntity::new,
					MICROCHIPS.values().stream().map(BlockHolder::get).toArray(Block[]::new)
			).build(null)
	);
	
	public static Set<BlockHolder> values()
	{
		return Set.copyOf(Registry.HOLDERS);
	}
	
	private static <BlockType extends Block> BlockHolder<BlockType> create(
			String id, String englishName,
			Function<BlockBehaviour.Properties, BlockType> blockCreator
	)
	{
		BlockHolder<BlockType> holder = new BlockHolder<>(
				LBR.id(id), englishName,
				Registry.BLOCKS, blockCreator
		);
		Registry.include(holder);
		return holder;
	}
	
	private static <BlockType extends Block, ItemType extends BlockItem> BlockWithItemHolder<BlockType, ItemType> create(
			String id, String englishName,
			Function<BlockBehaviour.Properties, BlockType> blockCreator,
			BiFunction<Block, Item.Properties, ItemType> itemCreator,
			SortOrder sortOrder
	)
	{
		BlockWithItemHolder<BlockType, ItemType> holder = new BlockWithItemHolder<>(
				LBR.id(id), englishName,
				Registry.BLOCKS, blockCreator,
				LBRItems.Registry.ITEMS, itemCreator
		);
		holder.item().sorted(sortOrder);
		Registry.include(holder);
		LBRItems.Registry.include(holder.item());
		return holder;
	}
	
	private static BlockHolder<MicrochipBlock> createMicrochip(DyeColor color, String colorEnglishName, int order)
	{
		final String colorId = color.getName();
		final String id = "%s_microchip".formatted(colorId);
		final String englishName = "%s Microchip".formatted(colorEnglishName);
		var holder = create(id, englishName, (p) -> new MicrochipBlock(p, color), BlockItem::new, LBRSortOrder.MICROCHIP.and(order));
		holder
				.withProperties((p) -> p
						.mapColor(MapColor.STONE)
						.destroyTime(4f)
						.requiresCorrectToolForDrops())
				.tag(LBRTags.Blocks.MICROCHIPS, BlockTags.MINEABLE_WITH_PICKAXE)
				.withLootTable(CommonLootTableBuilders::self)
				.withModel((block) -> (provider) ->
						provider.simpleBlockWithItem(block.get(), provider.models().getBuilder(block.identifier().id())
								.parent(new ModelFile.UncheckedModelFile("block/block"))
								.customLoader((parent, efh) -> new BasicCustomLoaderBuilder<>(LBR.id("microchip"), parent, efh)).end()
								.texture("particle", LBR.id("block/microchip/side/%s".formatted(colorId)))
								.texture("base_up", LBR.id("block/microchip/top/%s".formatted(colorId)))
								.texture("base", LBR.id("block/microchip/side/%s".formatted(colorId)))
								.texture("base_down", LBR.id("block/microchip/bottom/%s".formatted(colorId)))
								.texture("signal_on_overlay", LBR.id("block/microchip/signal_on_overlay"))
								.texture("signal_off_overlay", LBR.id("block/microchip/signal_off_overlay"))));
		holder.item().tag(LBRTags.Items.MICROCHIPS);
		return holder;
	}
}
