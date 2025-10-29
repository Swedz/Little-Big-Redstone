package net.swedz.little_big_redstone.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.widget.Bounds;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.compat.emi.recipe.ClearConfigEmiRecipe;
import net.swedz.little_big_redstone.compat.emi.recipe.CopyStickyNoteEmiRecipe;
import net.swedz.little_big_redstone.compat.emi.recipe.DataRetainingDyeEmiRecipe;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;
import net.swedz.little_big_redstone.recipe.DataRetainingDyeRecipe;

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
		
		for(var tag : DataRetainingDyeRecipe.getAcceptableTags())
		{
			registry.addRecipe(new DataRetainingDyeEmiRecipe(tag, LBR.id("/data_retaining_dyeable/%s".formatted(tag.location().getPath()))));
		}
		
		registry.addRecipe(new ClearConfigEmiRecipe(LBR.id("/clear_config")));
		
		registry.addRecipe(new CopyStickyNoteEmiRecipe(LBR.id("/copy_sticky_note")));
	}
}
