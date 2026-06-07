package net.swedz.little_big_redstone;

import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;

public final class LBRFonts
{
	public static FontDescription STICKY_NOTE = new FontDescription.Resource(LBR.id("sticky_note"));
	
	public static FontDescription logicComponent(String namespace)
	{
		return new FontDescription.Resource(Identifier.fromNamespaceAndPath(namespace, "logic_component"));
	}
}
