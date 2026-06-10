package net.swedz.little_big_redstone.datagen.client.provider.models;

import com.google.common.collect.Lists;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.ComponentContents;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRBlocks;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.tesseract.neoforge.model.ModelGenerators;
import net.swedz.tesseract.neoforge.registry.common.CommonModelBuilders;

import java.util.List;
import java.util.Optional;

final class ItemModelsDatagenProvider
{
	static void registerModels(ModelGenerators generators)
	{
		CommonModelBuilders.generated(LBRItems.REDSTONE_BIT).accept(generators);
		CommonModelBuilders.generated(LBRItems.REDSTONE_CIRCUIT_BOARD).accept(generators);
		
		for(var color : DyeColor.values())
		{
			microchip(color, generators);
			
			logicArray(color, generators);
			
			floppyDisks(color, generators);
			
			stickyNoteText(color, generators);
			stickyNote(color, generators);
		}
	}
	
	private static void microchip(DyeColor color, ModelGenerators generators)
	{
		var holder = LBRBlocks.microchip(color).item();
		CommonModelBuilders.block(holder).accept(generators);
	}
	
	private static void logicArray(DyeColor color, ModelGenerators generators)
	{
		var colorId = color.getName();
		var holder = LBRItems.logicArray(color);
		var modelId = ModelTemplates.FLAT_ITEM.create(
				holder.asItem(),
				TextureMapping.layer0(new Material(LBR.id("item/logic_array/%s".formatted(colorId)))),
				generators.item().modelOutput
		);
		generators.item().itemModelOutput.register(holder.asItem(), new ClientItem(ItemModelUtils.plainModel(modelId), ClientItem.Properties.DEFAULT));
	}
	
	private static void floppyDisks(DyeColor color, ModelGenerators generators)
	{
		var colorId = color.getName();
		var holder = LBRItems.floppyDisk(color);
		var modelId = ModelTemplates.FLAT_ITEM.create(
				holder.asItem(),
				TextureMapping.layer0(new Material(LBR.id("item/floppy_disk/%s".formatted(colorId)))),
				generators.item().modelOutput
		);
		generators.item().itemModelOutput.register(holder.asItem(), new ClientItem(ItemModelUtils.plainModel(modelId), ClientItem.Properties.DEFAULT));
	}
	
	private static void stickyNoteText(DyeColor textColor, ModelGenerators generators)
	{
		ModelTemplates.FLAT_ITEM.create(
				LBR.id("item/sticky_note/text/" + textColor.getName()),
				new TextureMapping()
						.put(TextureSlot.LAYER0, new Material(LBR.id("item/sticky_note/text/" + textColor.getName()))),
				generators.item().modelOutput
		);
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
	
	private static void stickyNote(DyeColor paperColor, ModelGenerators generators)
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
