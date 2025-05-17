package net.swedz.little_big_redstone.datagen.client.provider;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.tesseract.neoforge.helper.datagen.FontDatagenProvider;

public final class LogicComponentFontDatagenProvider extends FontDatagenProvider
{
	public LogicComponentFontDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getExistingFileHelper(), LBR.ID, "logic_component");
	}
	
	@Override
	protected void addCharacters()
	{
		for(var type : LogicTypes.values())
		{
			this.addBitmap(type.symbol(), LBR.id("font/logic/" + type.id()));
		}
	}
}
