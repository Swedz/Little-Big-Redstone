package net.swedz.little_big_redstone.datagen.client.provider;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.tesseract.neoforge.helper.datagen.FontDatagenProvider;

public final class LogicComponentFontDatagenProvider extends FontDatagenProvider
{
	public LogicComponentFontDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getExistingFileHelper(), LBR.ID, "logic_component");
	}
	
	@Override
	protected void addProviders()
	{
		for(var type : LBRLogicTypes.values())
		{
			int height = 8;
			int ascent = 7;
			if(type.is(LBRLogicTypes.DEBUGGER))
			{
				height = 10;
				ascent = 8;
			}
			this.addBitmap(type.symbol(), type.id().withPrefix("font/logic/"), height, ascent);
		}
	}
}
