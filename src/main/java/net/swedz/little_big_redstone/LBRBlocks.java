package net.swedz.little_big_redstone;

import com.google.common.collect.Sets;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlock;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.tesseract.neoforge.registry.SortOrder;
import net.swedz.tesseract.neoforge.registry.common.CommonLootTableBuilders;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;
import net.swedz.tesseract.neoforge.registry.holder.BlockWithItemHolder;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
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
	
	public static final BlockHolder<MicrochipBlock> MICROCHIP = create("microchip", "Microchip", MicrochipBlock::new, BlockItem::new, LBRSortOrder.BLOCKS).withProperties((p) -> p.mapColor(MapColor.STONE).destroyTime(4f).requiresCorrectToolForDrops()).tag(BlockTags.MINEABLE_WITH_PICKAXE).withLootTable(CommonLootTableBuilders::self).withModel(LBRBlocks::microchipBlockState).register();
	
	public static final Supplier<BlockEntityType<MicrochipBlockEntity>> MICROCHIP_ENTITY = Registry.BLOCK_ENTITIES.register("microchip", () -> BlockEntityType.Builder.of(MicrochipBlockEntity::new, MICROCHIP.get()).build(null));
	
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
	
	private static Consumer<BlockStateProvider> microchipBlockState(BlockHolder block)
	{
		return (builder) ->
		{
			Function<Direction, ResourceLocation> textureOn = (direction) -> LBR.id("block/microchip_%s_on".formatted(direction.toString().toLowerCase(Locale.ROOT)));
			Function<Direction, ResourceLocation> textureOff = (direction) -> LBR.id("block/microchip_%s_off".formatted(direction.toString().toLowerCase(Locale.ROOT)));
			AtomicInteger index = new AtomicInteger();
			builder.itemModels().getBuilder(block.identifier().id())
					.parent(builder.models().cubeAll(block.identifier().id(), LBR.id("block/microchip_on")));
			builder.getVariantBuilder(block.get()).forAllStates((state) ->
			{
				boolean up = state.getValue(MicrochipBlock.UP);
				boolean down = state.getValue(MicrochipBlock.DOWN);
				boolean north = state.getValue(MicrochipBlock.NORTH);
				boolean south = state.getValue(MicrochipBlock.SOUTH);
				boolean east = state.getValue(MicrochipBlock.EAST);
				boolean west = state.getValue(MicrochipBlock.WEST);
				return ConfiguredModel.builder()
						.modelFile(builder.models()
								.cube(
										"block/microchip/" + index.getAndIncrement(),
										down ? textureOn.apply(Direction.DOWN) : textureOff.apply(Direction.DOWN),
										up ? textureOn.apply(Direction.UP) : textureOff.apply(Direction.UP),
										north ? textureOn.apply(Direction.NORTH) : textureOff.apply(Direction.NORTH),
										south ? textureOn.apply(Direction.SOUTH) : textureOff.apply(Direction.SOUTH),
										east ? textureOn.apply(Direction.EAST) : textureOff.apply(Direction.EAST),
										west ? textureOn.apply(Direction.WEST) : textureOff.apply(Direction.WEST)
								)
								.texture("particle", LBR.id("block/microchip")))
						.build();
			});
		};
	}
}
