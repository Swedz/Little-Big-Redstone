package net.swedz.little_big_redstone;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;
import net.swedz.little_big_redstone.network.LBRPackets;
import net.swedz.tesseract.neoforge.capabilities.CapabilitiesListeners;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(LBR.ID)
public final class LBR
{
	public static final String ID   = "little_big_redstone";
	public static final String NAME = "Little Big Redstone";
	
	public static ResourceLocation id(String path)
	{
		return ResourceLocation.fromNamespaceAndPath(ID, path);
	}
	
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
	
	public LBR(IEventBus bus, ModContainer container)
	{
		LogicTypes.init();
		AwarenessTypes.init();
		
		LBRComponents.init(bus);
		LBRItems.init(bus);
		LBRBlocks.init(bus);
		LBREntities.init(bus);
		LBRCreativeTabs.init(bus);
		LBRMenus.init(bus);
		
		bus.addListener(RegisterPayloadHandlersEvent.class, LBRPackets::init);
		
		bus.addListener(FMLCommonSetupEvent.class, (event) ->
		{
			LBRItems.values().forEach(ItemHolder::triggerRegistrationListener);
			LBRBlocks.values().forEach(BlockHolder::triggerRegistrationListener);
		});
		
		bus.addListener(RegisterCapabilitiesEvent.class, (event) -> CapabilitiesListeners.triggerAll(ID, event));
	}
}
