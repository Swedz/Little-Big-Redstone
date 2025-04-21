package net.swedz.little_big_redstone;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class LBRTags
{
	public static final class Items
	{
		public static final TagKey<Item> STICKY_NOTES = TagKey.create(Registries.ITEM, LBR.id("sticky_notes"));
	}
	
	public static final class Blocks
	{
		public static final TagKey<Block> MICROCHIPS = TagKey.create(Registries.BLOCK, LBR.id("microchips"));
	}
}
