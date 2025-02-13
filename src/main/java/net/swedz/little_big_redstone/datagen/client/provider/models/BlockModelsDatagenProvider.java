package net.swedz.little_big_redstone.datagen.client.provider.models;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRBlocks;
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
			
			ResourceLocation texture = LBR.id("block/sticky_note_%s".formatted(colorId));
			this.models()
					.withExistingParent(id, "%s:block/sticky_note".formatted(LBR.ID))
					.renderType(ResourceLocation.withDefaultNamespace("cutout"))
					.texture("particle", texture)
					.texture("texture", texture);
		}
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
