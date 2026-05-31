package net.swedz.little_big_redstone.datagen.client.provider.models;

import com.google.common.collect.Lists;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.ComponentContents;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.tesseract.neoforge.model.ModelGenerators;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.List;
import java.util.Optional;

final class ItemModelsDatagenProvider
{
	static void registerModels(ModelGenerators generators)
	{
		for(ItemHolder<?> item : LBRItems.values())
		{
			if(item.hasModelProvider())
			{
				item.modelProvider().accept(generators);
			}
		}
		
		stickyNoteTexts(generators);
		stickyNotes(generators);
	}
	
	private static void stickyNoteTexts(ModelGenerators generators)
	{
		for(var textColor : DyeColor.values())
		{
			ModelTemplates.FLAT_ITEM.create(
					LBR.id("item/sticky_note/text/" + textColor.getName()),
					new TextureMapping()
							.put(TextureSlot.LAYER0, new Material(LBR.id("item/sticky_note/text/" + textColor.getName()))),
					generators.item().modelOutput
			);
		}
	}
	
	private static ItemModel.Unbaked stickyNoteText(ModelGenerators generators)
	{
		List<SelectItemModel.SwitchCase<DyeColor>> cases = Lists.newArrayList();
		for(var textColor : DyeColor.values())
		{
			cases.add(new SelectItemModel.SwitchCase<>(
					List.of(textColor),
					ItemModelUtils.plainModel(LBR.id("item/sticky_note/text/" + textColor.getName()))
			));
		}
		var property = new ComponentContents(LBRComponents.STICKY_NOTE_TEXT_COLOR.get());
		return ItemModelUtils.select(property, cases);
	}
	
	private static void stickyNotes(ModelGenerators generators)
	{
		for(var paperColor : DyeColor.values())
		{
			var item = LBRItems.stickyNote(paperColor);
			
			var paperModelId = ModelTemplates.FLAT_ITEM.create(
					LBR.id("item/sticky_note/paper/" + paperColor.getName()),
					new TextureMapping()
							.put(TextureSlot.LAYER0, new Material(LBR.id("item/sticky_note/paper/" + paperColor.getName()))),
					generators.item().modelOutput
			);
			
			generators.item().itemModelOutput.accept(
					item.asItem(),
					new CompositeModel.Unbaked(
							List.of(
									ItemModelUtils.plainModel(paperModelId),
									stickyNoteText(generators)
							),
							Optional.empty()
					)
			);
		}
	}
}
