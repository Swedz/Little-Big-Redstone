package net.swedz.little_big_redstone;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.Lazy;
import net.swedz.little_big_redstone.network.packet.OpenNoteBoardPacket;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public final class LBRKeybinds
{
	public static final class Registry
	{
		private static final Set<Keybind> MAPPINGS = Sets.newHashSet();
		
		private static void init(RegisterKeyMappingsEvent event)
		{
			MAPPINGS.forEach((m) -> event.register(m.holder().get()));
		}
		
		private static void include(Keybind mapping)
		{
			MAPPINGS.add(mapping);
		}
		
		public static Set<Keybind> getMappings()
		{
			return Collections.unmodifiableSet(MAPPINGS);
		}
	}
	
	public static void init(IEventBus bus)
	{
		bus.addListener(RegisterKeyMappingsEvent.class, LBRKeybinds.Registry::init);
		
		NeoForge.EVENT_BUS.addListener(
				ClientTickEvent.Post.class,
				(event) ->
				{
					for(var keybind : LBRKeybinds.Registry.getMappings())
					{
						while(keybind.holder().get().consumeClick())
						{
							keybind.action().run();
						}
					}
				}
		);
	}
	
	public static final String CATEGORY = Util.makeDescriptionId("key.categories", LBR.id(LBR.ID));
	
	public static final Keybind OPEN_NOTE_BOARD = create(
			"open_note_board",
			"Open Note Board",
			(id) -> new KeyMapping(
					id,
					KeyConflictContext.IN_GAME,
					InputConstants.Type.KEYSYM,
					GLFW.GLFW_KEY_UNKNOWN,
					CATEGORY
			),
			OpenNoteBoardPacket.INSTANCE::sendToServer
	);
	
	private static Keybind create(String id, String englishName, Function<String, KeyMapping> creator, Runnable action)
	{
		var descriptionId = Util.makeDescriptionId("key", LBR.id(id));
		var keybind = new Keybind(descriptionId, englishName, Lazy.of(() -> creator.apply(descriptionId)), action);
		Registry.include(keybind);
		return keybind;
	}
	
	public record Keybind(String descriptionId, String englishName, Lazy<KeyMapping> holder, Runnable action)
	{
	}
}
