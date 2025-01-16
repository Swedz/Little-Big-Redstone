package net.swedz.redstone_circuitry.datagen.client.provider;

import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.redstone_circuitry.RCItems;
import net.swedz.redstone_circuitry.RCText;
import net.swedz.redstone_circuitry.RedstoneCircuitry;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

public final class LanguageDatagenProvider extends LanguageProvider
{
	public LanguageDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), RedstoneCircuitry.ID, "en_us");
	}
	
	@Override
	protected void addTranslations()
	{
		for(RCText text : RCText.values())
		{
			this.add(text.getTranslationKey(), text.englishText());
		}
		
		for(ItemHolder item : RCItems.values())
		{
			this.add(item.asItem(), item.identifier().englishName());
		}
		
		this.add(RedstoneCircuitry.id(RedstoneCircuitry.ID).toLanguageKey("itemGroup"), RedstoneCircuitry.NAME);
	}
}
