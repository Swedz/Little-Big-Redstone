package net.swedz.redstone_circuitry;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.swedz.redstone_circuitry.microchip.logic.LogicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(RedstoneCircuitry.ID)
public final class RedstoneCircuitry
{
	public static final String ID   = "redstone_circuitry";
	public static final String NAME = "Redstone Circuitry";
	
	public static ResourceLocation id(String path)
	{
		return ResourceLocation.fromNamespaceAndPath(ID, path);
	}
	
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
	
	public RedstoneCircuitry(IEventBus bus, ModContainer container)
	{
		LogicTypes.init();
		
		RCComponents.init(bus);
		RCItems.init(bus);
		RCBlocks.init(bus);
		RCCreativeTabs.init(bus);
		RCMenus.init(bus);
	}
}
