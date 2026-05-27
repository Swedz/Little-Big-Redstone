package net.swedz.little_big_redstone.datagen.client.provider;

import com.google.common.collect.Sets;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.LBRKeybinds;
import net.swedz.tesseract.neoforge.lang.LangInstance;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.Set;

public final class LanguageDatagenProvider extends LanguageProvider
{
	private static final Set<LangInstance<?>> INSTANCES = Sets.newHashSet();
	
	public static void include(LangInstance<?> instance)
	{
		INSTANCES.add(instance);
	}
	
	public LanguageDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), LBR.ID, "en_us");
	}
	
	@Override
	protected void addTranslations()
	{
		for(var instance : INSTANCES)
		{
			instance.datagen(this);
		}
		
		for(ItemHolder item : LBRItems.values())
		{
			this.add(item.asItem(), item.identifier().englishName());
		}
		
		this.add(LBR.id(LBR.ID).toLanguageKey("itemGroup"), LBR.NAME);
		
		this.add(LBRKeybinds.CATEGORY.id().toLanguageKey("key.category"), LBR.NAME);
		
		for(var keybind : LBRKeybinds.Registry.getMappings())
		{
			this.add(keybind.descriptionId(), keybind.englishName());
		}
	}
}
