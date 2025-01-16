package net.swedz.my_mod;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MyMod.ID)
public final class MyMod
{
	public static final String ID   = "my_mod";
	public static final String NAME = "My Mod";
	
	public static ResourceLocation id(String path)
	{
		return ResourceLocation.fromNamespaceAndPath(ID, path);
	}
	
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
	
	public MyMod(IEventBus bus, ModContainer container)
	{
		LOGGER.info("Mod has been loaded!");
	}
}
