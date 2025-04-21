package net.swedz.little_big_redstone.helper;

import net.minecraft.world.item.DyeColor;
import org.apache.commons.lang3.function.TriConsumer;

import java.util.List;

public final class DyeColorHelper
{
	private static final List<DyeColor> ORDERED = List.of(
			DyeColor.WHITE,
			DyeColor.LIGHT_GRAY,
			DyeColor.GRAY,
			DyeColor.BLACK,
			DyeColor.BROWN,
			DyeColor.RED,
			DyeColor.ORANGE,
			DyeColor.YELLOW,
			DyeColor.LIME,
			DyeColor.GREEN,
			DyeColor.CYAN,
			DyeColor.LIGHT_BLUE,
			DyeColor.BLUE,
			DyeColor.PURPLE,
			DyeColor.MAGENTA,
			DyeColor.PINK
	);
	
	public static List<DyeColor> values()
	{
		return ORDERED;
	}
	
	public static int size()
	{
		return ORDERED.size();
	}
	
	public static DyeColor get(int index)
	{
		return ORDERED.get(index);
	}
	
	public static int indexOf(DyeColor color)
	{
		return ORDERED.indexOf(color);
	}
	
	public static void forEachIndexed(TriConsumer<DyeColor, String, Integer> action)
	{
		for(int i = 0; i < size(); i++)
		{
			var color = get(i);
			var englishName = ENGLISH_NAMES.get(i);
			action.accept(color, englishName, i);
		}
	}
	
	private static final List<String> ENGLISH_NAMES = List.of(
			"White",
			"Light Gray",
			"Gray",
			"Black",
			"Brown",
			"Red",
			"Orange",
			"Yellow",
			"Lime",
			"Green",
			"Cyan",
			"Light Blue",
			"Blue",
			"Purple",
			"Magenta",
			"Pink"
	);
	
	public static String getEnglishName(DyeColor color)
	{
		return ENGLISH_NAMES.get(indexOf(color));
	}
}
