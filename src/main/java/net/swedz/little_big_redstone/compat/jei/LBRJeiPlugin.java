package net.swedz.little_big_redstone.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;

import java.util.List;

@JeiPlugin
public final class LBRJeiPlugin implements IModPlugin
{
	@Override
	public ResourceLocation getPluginUid()
	{
		return LBR.id("jei");
	}
	
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration)
	{
		registration.addGuiContainerHandler(
				MicrochipScreen.class,
				new IGuiContainerHandler<>()
				{
					@Override
					public List<Rect2i> getGuiExtraAreas(MicrochipScreen screen)
					{
						var menu = screen.getMenu();
						if(menu.getLogicArrayItemHandler().shouldDisplay())
						{
							return List.of(new Rect2i(screen.getGuiLeft() - 83, screen.getGuiTop(), 83, 144));
						}
						return List.of();
					}
				}
		);
	}
}
