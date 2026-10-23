package net.swedz.little_big_redstone.datagen.client.provider.models;

import com.google.common.collect.Lists;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.client.model.stickynote.SelectItemStickyNoteType;
import net.swedz.little_big_redstone.client.model.stickynote.StickyNoteEntityModelData;
import net.swedz.tesseract.neoforge.model.ModelGenerators;

import java.util.List;
import java.util.Optional;

final class StickyNoteEntityModelsDatagenProvider
{
	static void registerModels(ModelGenerators generators)
	{
		List<SelectItemModel.SwitchCase<StickyNoteEntityModelData>> cases = Lists.newArrayList();
		for(var paperColor : DyeColor.values())
		{
			generateModel(generators, paperColor);
			
			for(var textColor : DyeColor.values())
			{
				appendModel(cases, paperColor, textColor, true);
				appendModel(cases, paperColor, textColor, false);
			}
		}
		generators.item().itemModelOutput.register(
				LBR.id("entity/sticky_note"),
				new ClientItem(
						new SelectItemModel.Unbaked(
								Optional.empty(),
								new SelectItemModel.UnbakedSwitch<>(
										new SelectItemStickyNoteType(),
										cases
								),
								Optional.empty()
						),
						ClientItem.Properties.DEFAULT
				)
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
			List<SelectItemModel.SwitchCase<StickyNoteEntityModelData>> cases,
			DyeColor paperColor,
			DyeColor textColor,
			boolean showText
	)
	{
		List<ItemModel.Unbaked> models = Lists.newArrayList();
		models.add(new CuboidItemModelWrapper.Unbaked(LBR.id("block/sticky_note/paper/" + paperColor.getName()), Optional.empty(), List.of()));
		if(showText)
		{
			models.add(new CuboidItemModelWrapper.Unbaked(LBR.id("block/sticky_note/text/" + textColor.getName()), Optional.empty(), List.of()));
		}
		
		cases.add(new SelectItemModel.SwitchCase<>(
				List.of(new StickyNoteEntityModelData(
						paperColor,
						showText,
						textColor
				)),
				new CompositeModel.Unbaked(models, Optional.empty())
		));
	}
}
