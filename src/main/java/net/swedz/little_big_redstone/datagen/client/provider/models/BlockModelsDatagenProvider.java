package net.swedz.little_big_redstone.datagen.client.provider.models;

import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
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
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;

import java.util.Map;
import java.util.Optional;

final class BlockModelsDatagenProvider
{
	static void registerModels(ModelGenerators generators)
	{
		for(BlockHolder<?> block : LBRBlocks.values())
		{
			if(block.hasModelProvider())
			{
				block.modelProvider().accept(generators);
			}
		}
		
		microchips(generators);
	}
	
	private static final ModelTemplate MICROCHIP_SIDE_OVERLAY = new ModelTemplate(
			Optional.of(LBR.id("block/microchip/side_overlay")),
			Optional.empty(),
			TextureSlot.TEXTURE,
			TextureSlot.PARTICLE
	);
	
	private static void microchips(ModelGenerators generators)
	{
		for(var color : DyeColor.values())
		{
			var colorId = color.getName();
			var block = LBRBlocks.microchip(color);
			
			ModelTemplates.CUBE_BOTTOM_TOP.create(
					block.get(),
					new TextureMapping()
							.put(TextureSlot.TOP, new Material(LBR.id("block/microchip/top/" + colorId)))
							.put(TextureSlot.BOTTOM, new Material(LBR.id("block/microchip/bottom/" + colorId)))
							.put(TextureSlot.SIDE, new Material(LBR.id("block/microchip/side/" + colorId))),
					generators.block().modelOutput
			);
			
			generators.block().blockStateOutput.accept(customModel(
					block.get(), new MicrochipBlockModel.Unbaked(
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
					)
			));
		}
		
		for(var direction : Direction.values())
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
	}
	
	private static MultiVariantGenerator customModel(Block block, CustomUnbakedBlockStateModel customModel)
	{
		return MultiVariantGenerator.dispatch(block, MultiVariant.of(new CustomBlockStateModelBuilder.Simple(customModel)));
	}
}
