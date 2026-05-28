package net.swedz.little_big_redstone;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
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
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlock;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.client.model.microchip.MicrochipBlockModel;
import net.swedz.tesseract.api.Assert;
import net.swedz.tesseract.neoforge.registry.SortOrder;
import net.swedz.tesseract.neoforge.registry.common.CommonLootTableBuilders;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;
import net.swedz.tesseract.neoforge.registry.holder.BlockWithItemHolder;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
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
			() -> new BlockEntityType(
					MicrochipBlockEntity::new,
					MICROCHIPS.values().stream().map(BlockHolder::get).toArray(Block[]::new)
			)
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
				.withModel((block) -> (generators) ->
				{
					// TODO 26.1 item model
					// TODO 26.1 this needs to be moved to datagen only classes
					generators.block().blockStateOutput.accept(customModel(block.get(), new MicrochipBlockModel.Unbaked(
							new MicrochipBlockModel.FaceTextures(
									Optional.of(new Material(LBR.id("block/microchip/side/%s".formatted(colorId)))),
									Optional.of(new Material(LBR.id("block/microchip/signal_on_overlay"))),
									Optional.of(new Material(LBR.id("block/microchip/signal_off_overlay")))
							),
							Map.of(
									Direction.UP,
									new MicrochipBlockModel.FaceTextures(
											Optional.of(new Material(LBR.id("block/microchip/top/%s".formatted(colorId)))),
											Optional.empty(),
											Optional.empty()
									),
									Direction.DOWN,
									new MicrochipBlockModel.FaceTextures(
											Optional.of(new Material(LBR.id("block/microchip/bottom/%s".formatted(colorId)))),
											Optional.empty(),
											Optional.empty()
									)
							)
					)));
				});
		holder.item().tag(LBRTags.Items.MICROCHIPS);
		return holder;
	}
	
	// TODO 26.1 this needs to be moved to datagen only classes
	private static MultiVariantGenerator customModel(Block block, CustomUnbakedBlockStateModel customModel)
	{
		return MultiVariantGenerator.dispatch(block, MultiVariant.of(new CustomBlockStateModelBuilder.Simple(customModel)));
	}
}
