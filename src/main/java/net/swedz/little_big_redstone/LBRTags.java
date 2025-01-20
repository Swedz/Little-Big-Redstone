package net.swedz.little_big_redstone;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class LBRTags
{
	public static final class Items
	{
		public static final TagKey<Item> MICROCHIP_WIRE = create("microchip_wire");
		
		private static TagKey<Item> create(String path)
		{
			return TagKey.create(Registries.ITEM, LBR.id(path));
		}
	}
}
