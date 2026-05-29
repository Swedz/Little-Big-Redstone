package net.swedz.little_big_redstone.datagen.client.provider.models;

import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.client.model.stickynote.StickyNoteEntityModel;
import net.swedz.tesseract.neoforge.model.ModelGenerators;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.Optional;

final class ItemModelsDatagenProvider
{
	private static ModelTemplate PAPER_TEMPLATE = new ModelTemplate(Optional.of(LBR.id("item/sticky_note_entity/paper")), Optional.empty(), TextureSlot.TEXTURE);
	
	static void registerModels(ModelGenerators generators)
	{
		for(ItemHolder<?> item : LBRItems.values())
		{
			if(item.hasModelProvider())
			{
				item.modelProvider().accept(generators);
			}
		}
		
		stickyNoteEntity(generators);
	}
	
	private static void stickyNoteEntity(ModelGenerators generators)
	{
		var textModelId = LBR.id("item/sticky_note_entity/text");
		for(var color : DyeColor.values())
		{
			var colorId = color.getName();
			var paperModelId = LBR.id("item/sticky_note_entity/%s_paper".formatted(colorId));
			
			PAPER_TEMPLATE.create(
					paperModelId,
					TextureMapping.defaultTexture(new Material(LBR.id("item/sticky_note_entity/%s".formatted(colorId)))),
					generators.item().modelOutput
			);
			
			generators.item().itemModelOutput.register(
					LBR.id("sticky_note_entity/%s_with_text".formatted(colorId)),
					new ClientItem(
							new StickyNoteEntityModel.Unbaked(
									paperModelId,
									Optional.of(textModelId)
							),
							ClientItem.Properties.DEFAULT
					)
			);
			
			generators.item().itemModelOutput.register(
					LBR.id("sticky_note_entity/%s_without_text".formatted(colorId)),
					new ClientItem(
							new StickyNoteEntityModel.Unbaked(
									paperModelId,
									Optional.empty()
							),
							ClientItem.Properties.DEFAULT
					)
			);
		}
	}
}
