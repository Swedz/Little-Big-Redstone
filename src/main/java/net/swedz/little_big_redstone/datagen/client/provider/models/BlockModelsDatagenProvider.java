package net.swedz.little_big_redstone.datagen.client.provider.models;

import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRBlocks;
import net.swedz.little_big_redstone.client.model.stickynote.entity.StickyNoteEntityModelLoaderBuilder;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;

public final class BlockModelsDatagenProvider extends BlockStateProvider
{
	public BlockModelsDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), LBR.ID, event.getExistingFileHelper());
	}
	
	@Override
	protected void registerStatesAndModels()
	{
		for(BlockHolder block : LBRBlocks.values())
		{
			if(block.hasModelProvider())
			{
				block.modelProvider().accept(this);
			}
		}
		
		for(var color : DyeColor.values())
		{
			String colorId = color.getName();
			String id = "%s_sticky_note".formatted(colorId);
			
			this.models().getBuilder(id)
					.customLoader(StickyNoteEntityModelLoaderBuilder::begin)
					.baseLayer(this.models().nested()
							.parent(new ModelFile.UncheckedModelFile(LBR.id("block/sticky_note_base")))
							.texture("texture", LBR.id("block/sticky_note_%s".formatted(colorId))))
					.textLayer(this.models().nested()
							.parent(new ModelFile.UncheckedModelFile(LBR.id("block/sticky_note_text")))
							.texture("texture", LBR.id("block/sticky_note_text")))
					.end()
					.texture("particle", LBR.id("block/sticky_note_%s".formatted(colorId)));
		}
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
