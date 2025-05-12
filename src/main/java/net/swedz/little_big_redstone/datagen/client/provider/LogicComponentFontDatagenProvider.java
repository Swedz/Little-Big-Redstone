package net.swedz.little_big_redstone.datagen.client.provider;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.api.FontDatagenProvider;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;

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
