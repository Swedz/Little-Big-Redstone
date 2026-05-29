package net.swedz.little_big_redstone.datagen.client.provider.models;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.tesseract.neoforge.model.ModelGenerators;

public final class ModelsDatagenDelegator extends ModelProvider
{
	public ModelsDatagenDelegator(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), LBR.ID);
	}
	
	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels)
	{
		var generators = new ModelGenerators(blockModels, itemModels);
		
		BlockModelsDatagenProvider.registerModels(generators);
		ItemModelsDatagenProvider.registerModels(generators);
		LogicItemModelsDatagenProvider.registerModels(generators);
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
