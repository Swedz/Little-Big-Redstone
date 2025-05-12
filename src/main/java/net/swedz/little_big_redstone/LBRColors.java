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
	
	public static int componentForeground(DyeColor color)
	{
		return ColorHelper.getVibrantColor(color);
	}
	
	public static int componentBackground(DyeColor color)
	{
		if(color == DyeColor.BLACK || color == DyeColor.GRAY)
		{
			return 0xFFFFFFFF;
		}
		else
		{
			return 0xFF000000;
		}
	}
	
	public static int microchipItem(DyeColor color)
	{
		// TODO replace this with separate textures
		return ColorHelper.getVibrantColor(color);
	}
	
	public static int stickyNoteBackground(DyeColor color)
	{
		return switch (color)
		{
			case WHITE -> 0xFFCED4D5;
			case ORANGE -> 0xFFE06100;
			case MAGENTA -> 0xFFA9309F;
			case LIGHT_BLUE -> 0xFF2389C7;
			case YELLOW -> 0xFFEFAE15;
			case LIME -> 0xFF5FA919;
			case PINK -> 0xFFD5668F;
			case GRAY -> 0xFF36393D;
			case LIGHT_GRAY -> 0xFF7D7D73;
			case CYAN -> 0xFF167889;
			case PURPLE -> 0xFF641F9C;
			case BLUE -> 0xFF2C2E8F;
			case BROWN -> 0xFF5F3B1F;
			case GREEN -> 0xFF495B24;
			case RED -> 0xFF8E2121;
			case BLACK -> 0xFF080A0F;
		};
	}
	
	public static int stickyNotePin(DyeColor color)
	{
		return switch (color)
		{
			case WHITE -> 0xFF696868;
			default -> 0xFFD1CFCF;
		};
	}
	
	public static int stickyNoteText(DyeColor color)
	{
		return switch (color)
		{
			case GRAY, BLACK -> 0xFFFFFFFF;
			default -> 0xFF000000;
		};
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
