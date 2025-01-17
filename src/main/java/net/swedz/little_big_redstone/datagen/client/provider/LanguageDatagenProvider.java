package net.swedz.little_big_redstone.datagen.client.provider;

import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

public final class LanguageDatagenProvider extends LanguageProvider
{
	public LanguageDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), LBR.ID, "en_us");
	}
	
	@Override
	protected void addTranslations()
	{
		for(LBRText text : LBRText.values())
		{
			this.add(text.getTranslationKey(), text.englishText());
		}
		
		for(ItemHolder item : LBRItems.values())
		{
			this.add(item.asItem(), item.identifier().englishName());
		}
		
		this.add(LBR.id(LBR.ID).toLanguageKey("itemGroup"), LBR.NAME);
	}
}
