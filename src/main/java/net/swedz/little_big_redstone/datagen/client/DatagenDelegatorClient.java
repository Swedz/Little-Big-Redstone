package net.swedz.little_big_redstone.datagen.client;

import net.minecraft.data.DataProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.datagen.client.provider.LanguageDatagenProvider;
import net.swedz.little_big_redstone.datagen.client.provider.LogicComponentFontDatagenProvider;
import net.swedz.little_big_redstone.datagen.client.provider.StickyNoteFontDatagenProvider;
import net.swedz.little_big_redstone.datagen.client.provider.models.ModelsDatagenDelegator;

import java.util.function.Function;

public final class DatagenDelegatorClient
{
	public static void configure(GatherDataEvent event)
	{
		add(event, ModelsDatagenDelegator::new);
		add(event, LanguageDatagenProvider::new);
		add(event, LogicComponentFontDatagenProvider::new);
		add(event, StickyNoteFontDatagenProvider::new);
	}
	
	private static void add(GatherDataEvent event, Function<GatherDataEvent, DataProvider> providerCreator)
	{
		event.addProvider(providerCreator.apply(event));
	}
}
