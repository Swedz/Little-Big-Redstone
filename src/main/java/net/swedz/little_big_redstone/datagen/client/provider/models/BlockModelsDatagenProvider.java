package net.swedz.little_big_redstone.datagen.client.provider.models;

import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRBlocks;
import net.swedz.little_big_redstone.client.model.microchip.MicrochipBlockModel;
import net.swedz.tesseract.neoforge.model.ModelGenerators;

import java.util.Map;
import java.util.Optional;

final class BlockModelsDatagenProvider
{
	static void registerModels(ModelGenerators generators)
	{
		for(var color : DyeColor.values())
		{
			microchip(color, generators);
		}
		
		microchipOverlays(generators);
	}
	
	private static final TextureSlot TOP_OVERLAY = TextureSlot.create("top_overlay");
	private static final TextureSlot BOTTOM_OVERLAY = TextureSlot.create("bottom_overlay");
	private static final TextureSlot SIDE_OVERLAY = TextureSlot.create("side_overlay");
	
	private static final ModelTemplate MICROCHIP = new ModelTemplate(
			Optional.of(LBR.id("block/microchip")),
			Optional.empty(),
			TextureSlot.TOP,
			TextureSlot.BOTTOM,
			TextureSlot.SIDE,
			TOP_OVERLAY,
			BOTTOM_OVERLAY,
			SIDE_OVERLAY,
			TextureSlot.PARTICLE
	);
	
	private static void microchip(DyeColor color, ModelGenerators generators)
	{
		var colorId = color.getName();
		var block = LBRBlocks.microchip(color);
		
		MICROCHIP.create(
				block.get(),
				new TextureMapping()
						.put(TextureSlot.PARTICLE, new Material(LBR.id("block/microchip/side/base")))
						.put(TextureSlot.TOP, new Material(LBR.id("block/microchip/top/base")))
						.put(TextureSlot.BOTTOM, new Material(LBR.id("block/microchip/bottom/base")))
						.put(TextureSlot.SIDE, new Material(LBR.id("block/microchip/side/base")))
						.put(TOP_OVERLAY, new Material(LBR.id("block/microchip/top/" + colorId)))
						.put(BOTTOM_OVERLAY, new Material(LBR.id("block/microchip/bottom/" + colorId)))
						.put(SIDE_OVERLAY, new Material(LBR.id("block/microchip/side/" + colorId))),
				generators.block().modelOutput
		);
		
		generators.block().blockStateOutput.accept(customModel(
				block.get(),
				new MicrochipBlockModel.Unbaked(
						new MicrochipBlockModel.FaceTextures(
								Optional.of(new Material(LBR.id("block/microchip/side/base"))),
								Optional.of(new Material(LBR.id("block/microchip/side/%s".formatted(colorId)))),
								Optional.of(new Material(LBR.id("block/microchip/signal_on_overlay"))),
								Optional.of(new Material(LBR.id("block/microchip/signal_off_overlay")))
						),
						Map.of(
								Direction.UP,
								new MicrochipBlockModel.FaceTextures(
										Optional.of(new Material(LBR.id("block/microchip/top/base"))),
										Optional.of(new Material(LBR.id("block/microchip/top/%s".formatted(colorId)))),
										Optional.empty(),
										Optional.empty()
								),
								Direction.DOWN,
								new MicrochipBlockModel.FaceTextures(
										Optional.of(new Material(LBR.id("block/microchip/bottom/base"))),
										Optional.of(new Material(LBR.id("block/microchip/bottom/%s".formatted(colorId)))),
										Optional.empty(),
										Optional.empty()
								)
						)
				)
		));
	}
	
	private static final ModelTemplate MICROCHIP_SIDE_OVERLAY = new ModelTemplate(
			Optional.of(LBR.id("block/microchip/side_overlay")),
			Optional.empty(),
			TextureSlot.TEXTURE,
			TextureSlot.PARTICLE
	);
	
	private static void microchipOverlay(Direction direction, ModelGenerators generators)
	{
		var texture = new Material(LBR.id("block/microchip/overlay_" + direction.getName()));
		MICROCHIP_SIDE_OVERLAY.create(
				LBR.id("block/microchip/side_overlay_" + direction.getName()),
				new TextureMapping()
						.put(TextureSlot.TEXTURE, texture)
						.put(TextureSlot.PARTICLE, texture),
				generators.block().modelOutput
		);
	}
	
	private static void microchipOverlays(ModelGenerators generators)
	{
		for(var direction : Direction.values())
		{
			microchipOverlay(direction, generators);
		}
	}
	
	private static MultiVariantGenerator customModel(Block block, CustomUnbakedBlockStateModel customModel)
	{
		return MultiVariantGenerator.dispatch(block, MultiVariant.of(new CustomBlockStateModelBuilder.Simple(customModel)));
	}
}
