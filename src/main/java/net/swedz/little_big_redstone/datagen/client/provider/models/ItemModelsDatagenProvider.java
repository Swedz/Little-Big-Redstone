package net.swedz.little_big_redstone.datagen.client.provider.models;

import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

public final class ItemModelsDatagenProvider extends ItemModelProvider
{
	public ItemModelsDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), LBR.ID, event.getExistingFileHelper());
	}
	
	@Override
	protected void registerModels()
	{
		for(ItemHolder item : LBRItems.values())
		{
			if(item.hasModelProvider())
			{
				item.modelProvider().accept(this);
			}
		}
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
