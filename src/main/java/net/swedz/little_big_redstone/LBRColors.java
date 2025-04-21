package net.swedz.little_big_redstone;

import net.minecraft.world.item.DyeColor;
import net.swedz.tesseract.neoforge.helper.ColorHelper;
import org.apache.commons.lang3.function.TriConsumer;

import java.util.List;

public final class LBRColors
{
	public static int circuitboard(DyeColor color)
	{
		// TODO replace this with separate textures
		return 0xFF000000 | switch (color)
		{
			case WHITE -> 0xCCCCCC;
			case ORANGE -> 0xFFBD7F;
			case MAGENTA -> 0xFF7FFF;
			case LIGHT_BLUE -> 0x7FFFFF;
			case YELLOW -> 0xFFFF7F;
			case LIME -> 0x7FFF7F;
			case PINK -> 0xFFD8FF;
			case GRAY -> 0x808080;
			case LIGHT_GRAY -> 0xA6A6A6;
			case CYAN -> 0x54A8A8;
			case PURPLE -> 0xA854A8;
			case BLUE -> 0x7F7FFF;
			case BROWN -> 0x89664E;
			case GREEN -> 0x54A854;
			case RED -> 0xFF7F7F;
			case BLACK -> 0x333333;
			default -> color.getTextColor() & 0x00FFFFFF;
		};
	}
	
	public static int component(DyeColor color)
	{
		return ColorHelper.getVibrantColor(color);
	}
	
	public static int microchipItem(DyeColor color)
	{
		// TODO replace this with separate textures
		return ColorHelper.getVibrantColor(color);
	}
	
	private static final List<DyeColor> COLORS = List.of(
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
	
	public static void forEachIndexed(TriConsumer<DyeColor, String, Integer> action)
	{
		for(int i = 0; i < COLORS.size(); i++)
		{
			var color = COLORS.get(i);
			var englishName = ENGLISH_NAMES.get(i);
			action.accept(color, englishName, i);
		}
	}
}
