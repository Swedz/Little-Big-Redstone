package net.swedz.little_big_redstone;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class LBRTags
{
	public static final class Items
	{
		public static final TagKey<Item> DYE_WASHER          = TagKey.create(Registries.ITEM, LBR.id("dye_washer"));
		public static final TagKey<Item> DYE_WASHER_CONSUMED = TagKey.create(Registries.ITEM, LBR.id("dye_washer/consumed"));
		
		public static final TagKey<Item> STICKY_NOTE_SEALANT = TagKey.create(Registries.ITEM, LBR.id("sticky_note_sealant"));
		
		public static final TagKey<Item> MICROCHIPS       = TagKey.create(Registries.ITEM, LBR.id("microchips"));
		public static final TagKey<Item> LOGIC_COMPONENTS = TagKey.create(Registries.ITEM, LBR.id("logic_components"));
		public static final TagKey<Item> LOGIC_ARRAYS     = TagKey.create(Registries.ITEM, LBR.id("logic_arrays"));
		public static final TagKey<Item> FLOPPY_DISKS     = TagKey.create(Registries.ITEM, LBR.id("floppy_disks"));
		public static final TagKey<Item> STICKY_NOTES     = TagKey.create(Registries.ITEM, LBR.id("sticky_notes"));
	}
	
	public static final class Blocks
	{
		public static final TagKey<Block> MICROCHIPS = TagKey.create(Registries.BLOCK, LBR.id("microchips"));
	}
}
