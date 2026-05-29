package net.swedz.little_big_redstone.datagen.client.provider.models;

import net.swedz.little_big_redstone.LBRBlocks;
import net.swedz.tesseract.neoforge.model.ModelGenerators;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;

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
	}
}
