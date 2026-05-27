package net.swedz.little_big_redstone.datagen.client.provider.models;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRBlocks;
import net.swedz.tesseract.neoforge.model.ModelGenerators;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;

public final class BlockModelsDatagenProvider extends ModelProvider
{
	public BlockModelsDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), LBR.ID);
	}
	
	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels)
	{
		var generators = new ModelGenerators(blockModels, itemModels);
		for(BlockHolder<?> block : LBRBlocks.values())
		{
			if(block.hasModelProvider())
			{
				block.modelProvider().accept(generators);
			}
		}
		
		// TODO 26.1
		/*for(var color : DyeColor.values())
		{
			String colorId = color.getName();
			String id = "%s_sticky_note".formatted(colorId);
			
			this.models().getBuilder(id)
					.customLoader(StickyNoteEntityModelLoaderBuilder::begin)
					.baseLayer(this.models().nested()
							.parent(new ModelFile.UncheckedModelFile(LBR.id("block/sticky_note_base")))
							.texture("texture", LBR.id("block/sticky_note/%s".formatted(colorId))))
					.textLayer(this.models().nested()
							.parent(new ModelFile.UncheckedModelFile(LBR.id("block/sticky_note_text")))
							.texture("texture", LBR.id("block/sticky_note/text")))
					.end()
					.texture("particle", LBR.id("block/sticky_note/%s".formatted(colorId)));
		}*/
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
