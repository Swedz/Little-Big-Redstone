package net.swedz.little_big_redstone;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;

import java.util.function.Supplier;

public final class LBRMenus
{
	private static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, LBR.ID);
	
	public static final Supplier<MenuType<MicrochipMenu>> MICROCHIP = REGISTRY.register("microchip", () -> new MenuType<>(MicrochipMenu::new, FeatureFlags.DEFAULT_FLAGS));
	
	public static void init(IEventBus bus)
	{
		REGISTRY.register(bus);
	}
}
