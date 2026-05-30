package net.swedz.little_big_redstone.datagen.client.provider.models;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.PropertyValueList;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.block.CompositeBlockModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;
import net.swedz.tesseract.neoforge.model.ModelGenerators;

import java.util.List;
import java.util.Map;
import java.util.Optional;

final class StickyNoteEntityModelsDatagenProvider
{
	static void registerModels(ModelGenerators generators)
	{
		Map<List<Property.Value<?>>, CustomUnbakedBlockStateModel> variants = Maps.newHashMap();
		for(var paperColor : DyeColor.values())
		{
			generateModel(generators, paperColor);
			
			for(var textColor : DyeColor.values())
			{
				appendModel(variants, paperColor, textColor, true);
				appendModel(variants, paperColor, textColor, false);
			}
		}
		generators.block().blockStateOutputById().register(
				LBR.id("sticky_note"),
				dispatch(variants)
		);
	}
	
	private static ModelTemplate PAPER_TEMPLATE = new ModelTemplate(
			Optional.of(LBR.id("block/sticky_note/paper")),
			Optional.empty(),
			TextureSlot.TEXTURE,
			TextureSlot.PARTICLE
	);
	
	private static ModelTemplate TEXT_TEMPLATE = new ModelTemplate(
			Optional.of(LBR.id("block/sticky_note/text")),
			Optional.empty(),
			TextureSlot.TEXTURE,
			TextureSlot.PARTICLE
	);
	
	private static void generateModel(
			ModelGenerators generators,
			DyeColor color
	)
	{
		var paperTexture = new Material(LBR.id("block/sticky_note/paper/" + color.getName()));
		PAPER_TEMPLATE.create(
				LBR.id("block/sticky_note/paper/" + color.getName()),
				new TextureMapping()
						.put(TextureSlot.TEXTURE, paperTexture)
						.put(TextureSlot.PARTICLE, paperTexture),
				generators.block().modelOutput
		);
		
		var textTexture = new Material(LBR.id("block/sticky_note/text/" + color.getName()));
		TEXT_TEMPLATE.create(
				LBR.id("block/sticky_note/text/" + color.getName()),
				new TextureMapping()
						.put(TextureSlot.TEXTURE, textTexture)
						.put(TextureSlot.PARTICLE, textTexture),
				generators.block().modelOutput
		);
	}
	
	private static void appendModel(
			Map<List<Property.Value<?>>, CustomUnbakedBlockStateModel> variants,
			DyeColor paperColor,
			DyeColor textColor,
			boolean showText
	)
	{
		List<BlockStateModel.Unbaked> models = Lists.newArrayList();
		models.add(new SingleVariant.Unbaked(new Variant(LBR.id("block/sticky_note/paper/" + paperColor.getName()))));
		if(showText)
		{
			models.add(new SingleVariant.Unbaked(new Variant(LBR.id("block/sticky_note/text/" + textColor.getName()))));
		}
		
		variants.put(
				List.of(
						new Property.Value<>(StickyNoteEntity.BLOCKSTATE_PAPER_COLOR, paperColor),
						new Property.Value<>(StickyNoteEntity.BLOCKSTATE_TEXT_COLOR, textColor),
						new Property.Value<>(StickyNoteEntity.BLOCKSTATE_SHOW_TEXT, showText)
				),
				new CompositeBlockModel.Unbaked(models)
		);
	}
	
	private static BlockStateModelDispatcher dispatch(Map<List<Property.Value<?>>, CustomUnbakedBlockStateModel> variants)
	{
		Map<String, BlockStateModel.Unbaked> variantsUnbaked = Maps.newHashMap();
		for(var entry : variants.entrySet())
		{
			variantsUnbaked.put(
					new PropertyValueList(entry.getKey()).getKey(),
					MultiVariant.of(new CustomBlockStateModelBuilder.Simple(entry.getValue())).toUnbaked()
			);
		}
		return new BlockStateModelDispatcher(Optional.of(new BlockStateModelDispatcher.SimpleModelSelectors(variantsUnbaked)), Optional.empty());
	}
}
