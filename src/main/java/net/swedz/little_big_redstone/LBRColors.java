package net.swedz.little_big_redstone;

import net.minecraft.world.item.DyeColor;
import org.apache.commons.lang3.function.TriConsumer;

import java.util.List;

public final class LBRColors
{
	public static int circuitboard(DyeColor color)
	{
		return 0xFF000000 | switch (color)
		{
			case WHITE -> 0x838C96;
			case ORANGE -> 0x8D2E00;
			case MAGENTA -> 0x922C8C;
			case LIGHT_BLUE -> 0x27588B;
			case YELLOW -> 0x876709;
			case LIME -> 0x356008;
			case PINK -> 0x9F215E;
			case GRAY -> 0x53516B;
			case LIGHT_GRAY -> 0x5F6A7B;
			case CYAN -> 0x19636E;
			case PURPLE -> 0x632285;
			case BLUE -> 0x101DA1;
			case BROWN -> 0x6A2E0F;
			case GREEN -> 0x155912;
			case RED -> 0x720000;
			case BLACK -> 0x41334F;
			default -> color.getTextColor() & 0x00FFFFFF;
		};
	}
	
	public static int componentForeground(DyeColor color)
	{
		return 0xFF000000 | switch (color)
		{
			case WHITE -> 0xEFF5FF;
			case ORANGE -> 0xFF920F;
			case MAGENTA -> 0xFF80F7;
			case LIGHT_BLUE -> 0x6AE4FF;
			case YELLOW -> 0xFFE800;
			case LIME -> 0xA4EB00;
			case PINK -> 0xFFB8EA;
			case GRAY -> 0xA8ABBF;
			case LIGHT_GRAY -> 0xC5D2E0;
			case CYAN -> 0x47D5AF;
			case PURPLE -> 0xD857FF;
			case BLUE -> 0x4BA6F1;
			case BROWN -> 0xC66A1A;
			case GREEN -> 0x18AF2F;
			case RED -> 0xFF0000;
			case BLACK -> 0x726D81;
			default -> color.getTextColor() & 0x00FFFFFF;
		};
	}
	
	public static int componentBackground(DyeColor color)
	{
		return 0xFF000000;
	}
	
	public static int stickyNoteText(DyeColor color)
	{
		return 0xFF000000 | color.getTextColor();
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
