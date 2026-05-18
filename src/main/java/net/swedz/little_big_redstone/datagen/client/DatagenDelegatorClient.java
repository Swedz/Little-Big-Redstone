package net.swedz.little_big_redstone.datagen.client;

import net.minecraft.data.DataProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.datagen.client.provider.LanguageDatagenProvider;
import net.swedz.little_big_redstone.datagen.client.provider.LogicComponentFontDatagenProvider;
import net.swedz.little_big_redstone.datagen.client.provider.StickyNoteFontDatagenProvider;
import net.swedz.little_big_redstone.datagen.client.provider.models.BlockModelsDatagenProvider;
import net.swedz.little_big_redstone.datagen.client.provider.models.ItemModelsDatagenProvider;
import net.swedz.little_big_redstone.datagen.client.provider.models.LogicItemModelsDatagenProvider;

import java.util.function.Function;

public final class DatagenDelegatorClient
{
	public static void configure(GatherDataEvent event)
	{
		add(event, BlockModelsDatagenProvider::new);
		add(event, ItemModelsDatagenProvider::new);
		add(event, LogicItemModelsDatagenProvider::new);
		add(event, LanguageDatagenProvider::new);
		add(event, LogicComponentFontDatagenProvider::new);
		add(event, StickyNoteFontDatagenProvider::new);
	}
	
	private static void add(GatherDataEvent event, Function<GatherDataEvent, DataProvider> providerCreator)
	{
		event.getGenerator().addProvider(event.includeClient(), providerCreator.apply(event));
	}
}
