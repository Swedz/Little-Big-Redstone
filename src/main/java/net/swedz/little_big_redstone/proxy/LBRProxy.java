package net.swedz.little_big_redstone.proxy;

import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;
import net.swedz.tesseract.neoforge.proxy.Proxy;
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint;

import java.util.List;

@ProxyEntrypoint
public class LBRProxy implements Proxy
{
	protected MinecraftServerAudiences serverAudiences;
	
	@Override
	public void init()
	{
		NeoForge.EVENT_BUS.addListener(ServerAboutToStartEvent.class, (event) -> serverAudiences = MinecraftServerAudiences.of(event.getServer()));
		NeoForge.EVENT_BUS.addListener(ServerStoppedEvent.class, (event) -> serverAudiences = null);
	}
	
	public void handleUpdateMicrochip(int containerId, Microchip microchip)
	{
	}
	
	public void handleUpdateComponentsMicrochip(int containerId, List<LogicEntry> entries)
	{
	}
	
	public void openStickyNote(int entityId, DyeColor color, DyeColor textColor, String text, boolean edit)
	{
	}
	
	public void floppyDiskGuiOverlayUpdate(boolean force)
	{
	}
	
	public Component adventureToNative(net.kyori.adventure.text.Component adventure)
	{
		return serverAudiences.asNative(adventure);
	}
	
	public net.kyori.adventure.text.Component nativeToAdventure(Component vanilla)
	{
		return serverAudiences.asAdventure(vanilla);
	}
}
