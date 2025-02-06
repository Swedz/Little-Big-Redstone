package net.swedz.little_big_redstone;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlock;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.block.stickynote.StickyNoteBlock;
import net.swedz.little_big_redstone.block.stickynote.StickyNoteBlockEntity;
import net.swedz.tesseract.neoforge.registry.SortOrder;
import net.swedz.tesseract.neoforge.registry.common.CommonLootTableBuilders;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;
import net.swedz.tesseract.neoforge.registry.holder.BlockWithItemHolder;

import java.util.List;
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
	
	public static final BlockHolder<MicrochipBlock> MICROCHIP = create("microchip", "Microchip", MicrochipBlock::new, BlockItem::new, LBRSortOrder.MICROCHIP).withProperties((p) -> p.mapColor(MapColor.STONE).destroyTime(4f).requiresCorrectToolForDrops()).tag(BlockTags.MINEABLE_WITH_PICKAXE).withLootTable(CommonLootTableBuilders::self).withModel(LBRBlocks::microchipBlockState).register();
	
	public static final Supplier<BlockEntityType<MicrochipBlockEntity>> MICROCHIP_ENTITY = Registry.BLOCK_ENTITIES.register("microchip", () -> BlockEntityType.Builder.of(MicrochipBlockEntity::new, MICROCHIP.get()).build(null));
	
	public static final Supplier<BlockEntityType<StickyNoteBlockEntity>> STICKY_NOTE_ENTITY;
	
	static
	{
		List<BlockHolder> stickyNoteBlocks = Lists.newArrayList();
		List<DyeColor> colors = List.of(
				DyeColor.WHITE,
				DyeColor.LIGHT_GRAY,
				DyeColor.GRAY,
				DyeColor.BLACK,
				DyeColor.BROWN,
				DyeColor.RED,
				DyeColor.ORANGE,
				DyeColor.YELLOW,
				DyeColor.LIME,
				DyeColor.GREEN,
				DyeColor.CYAN,
				DyeColor.LIGHT_BLUE,
				DyeColor.BLUE,
				DyeColor.PURPLE,
				DyeColor.MAGENTA,
				DyeColor.PINK
		);
		List<String> colorNames = List.of(
				"White",
				"Light Gray",
				"Gray",
				"Black",
				"Brown",
				"Red",
				"Orange",
				"Yellow",
				"Lime",
				"Green",
				"Cyan",
				"Light Blue",
				"Blue",
				"Purple",
				"Magenta",
				"Pink"
		);
		for(int i = 0; i < colors.size(); i++)
		{
			var color = colors.get(i);
			var colorName = colorNames.get(i);
			var block = createStickyNote(color, colorName, i).register();
			stickyNoteBlocks.add(block);
		}
		
		STICKY_NOTE_ENTITY = Registry.BLOCK_ENTITIES.register(
				"sticky_note",
				() -> BlockEntityType.Builder
						.of(
								(pos, state) -> new StickyNoteBlockEntity(pos, state, state.getBlock() instanceof StickyNoteBlock block ? block.color() : DyeColor.WHITE),
								stickyNoteBlocks.stream().map(BlockHolder::get).toArray(Block[]::new)
						)
						.build(null)
		);
	}
	
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
	
	private static BlockWithItemHolder<StickyNoteBlock, BlockItem> createStickyNote(DyeColor color, String colorEnglishName, int order)
	{
		final String colorId = color.getName();
		final String id = "%s_sticky_note".formatted(colorId);
		final String englishName = "%s Sticky Note".formatted(colorEnglishName);
		
		BlockWithItemHolder<StickyNoteBlock, BlockItem> block = create(id, englishName, (p) -> new StickyNoteBlock(p, color), BlockItem::new, LBRSortOrder.STICKY_NOTES.and(order));
		
		block.withLootTable(CommonLootTableBuilders::self);
		block.withProperties((p) -> p
				.mapColor(MapColor.STONE)
				.sound(SoundType.WOOL)
				.instabreak()
				.noCollission()
				.forceSolidOn()
				.pushReaction(PushReaction.DESTROY));
		block.tag(LBRTags.Blocks.STICKY_NOTES);
		
		block.withModel((holder) -> (provider) ->
		{
			ResourceLocation texture = LBR.id("block/sticky_note_%s".formatted(colorId));
			ModelFile model = provider.models()
					.withExistingParent(holder.identifier().id(), "%s:block/sticky_note".formatted(LBR.ID))
					.renderType(ResourceLocation.withDefaultNamespace("cutout"))
					.texture("particle", texture)
					.texture("texture", texture);
			provider.getVariantBuilder(holder.get()).forAllStates((state) ->
			{
				Direction facing = state.getValue(StickyNoteBlock.FACING);
				AttachFace face = state.getValue(StickyNoteBlock.FACE);
				return ConfiguredModel.builder()
						.modelFile(model)
						.rotationX(face == AttachFace.FLOOR ? 0 : (face == AttachFace.WALL ? 90 : 180))
						.rotationY((int) (face == AttachFace.WALL ? facing.getOpposite() : facing).toYRot())
						.build();
			});
		});
		
		block.item().tag(LBRTags.Items.STICKY_NOTES);
		block.item().withModel((holder) -> (provider) ->
				provider.getBuilder(holder.identifier().id())
						.parent(new ModelFile.UncheckedModelFile("item/generated"))
						.texture("layer0", "%s:item/sticky_note_%s".formatted(LBR.ID, colorId)));
		
		return block;
	}
}
