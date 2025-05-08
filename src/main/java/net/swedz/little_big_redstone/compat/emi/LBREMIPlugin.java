package net.swedz.little_big_redstone.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.widget.Bounds;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;

@EmiEntrypoint
public final class LBREMIPlugin implements EmiPlugin
{
	@Override
	public void register(EmiRegistry registry)
	{
		registry.addExclusionArea(MicrochipScreen.class, (screen, bounds) ->
		{
			var menu = screen.getMenu();
			if(menu.getLogicArrayItemHandler().shouldDisplay())
			{
				bounds.accept(new Bounds(screen.getGuiLeft() - 83, screen.getGuiTop(), 83, 144));
			}
		});
	}
}
