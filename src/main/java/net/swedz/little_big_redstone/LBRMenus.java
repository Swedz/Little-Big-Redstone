package net.swedz.little_big_redstone;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.gui.logicarray.LogicArrayMenu;
import net.swedz.little_big_redstone.gui.logicconfig.LogicConfigMenu;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.gui.noteboard.NoteBoardMenu;

import java.util.function.Supplier;

public final class LBRMenus
{
	private static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, LBR.ID);
	
	public static final Supplier<MenuType<MicrochipMenu>>   MICROCHIP    = REGISTRY.register("microchip", () -> new MenuType<>((IContainerFactory<MicrochipMenu>) MicrochipMenu::new, FeatureFlags.DEFAULT_FLAGS));
	public static final Supplier<MenuType<LogicConfigMenu>> LOGIC_CONFIG = REGISTRY.register("logic_config", () -> new MenuType<>((IContainerFactory<LogicConfigMenu>) LogicConfigMenu::new, FeatureFlags.DEFAULT_FLAGS));
	public static final Supplier<MenuType<LogicArrayMenu>>  LOGIC_ARRAY  = REGISTRY.register("logic_array", () -> new MenuType<>((IContainerFactory<LogicArrayMenu>) LogicArrayMenu::read, FeatureFlags.DEFAULT_FLAGS));
	public static final Supplier<MenuType<NoteBoardMenu>>   NOTE_BOARD   = REGISTRY.register("note_board", () -> new MenuType<>(NoteBoardMenu::new, FeatureFlags.DEFAULT_FLAGS));
	
	public static void init(IEventBus bus)
	{
		REGISTRY.register(bus);
	}
}
