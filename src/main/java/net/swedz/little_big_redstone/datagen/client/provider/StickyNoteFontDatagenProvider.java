package net.swedz.little_big_redstone.datagen.client.provider;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.tesseract.neoforge.helper.datagen.FontDatagenProvider;

import java.util.Map;

public final class StickyNoteFontDatagenProvider extends FontDatagenProvider
{
	public StickyNoteFontDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), LBR.ID, "sticky_note");
	}
	
	@Override
	protected void addProviders()
	{
		this.addSpace(Map.of(
				' ', 8f,
				'1', 1f
		));
		this.addBitmap('-', LBR.id("font/sticky_note/bulletpoint"), 8, 7);
		this.addBitmap('o', LBR.id("font/sticky_note/checkbox_unchecked"), 8, 7);
		this.addBitmap('x', LBR.id("font/sticky_note/checkbox_checked"), 8, 7);
	}
}
