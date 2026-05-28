package net.swedz.little_big_redstone.datagen.client.provider;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.tesseract.neoforge.helper.datagen.FontDatagenProvider;

public final class LogicComponentFontDatagenProvider extends FontDatagenProvider
{
	public LogicComponentFontDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), LBR.ID, "logic_component");
	}
	
	@Override
	protected void addProviders()
	{
		for(var type : LogicTypes.values())
		{
			int height = 8;
			int ascent = 7;
			if(type == LogicTypes.DEBUGGER)
			{
				height = 10;
				ascent = 8;
			}
			this.addBitmap(type.symbol(), LBR.id("font/logic/" + type.id()), height, ascent);
		}
	}
}
