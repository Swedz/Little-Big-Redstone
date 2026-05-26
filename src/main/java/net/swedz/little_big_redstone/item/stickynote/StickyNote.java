package net.swedz.little_big_redstone.item.stickynote;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.fml.loading.FMLEnvironment;
import net.swedz.little_big_redstone.LBRFonts;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StickyNote
{
	private static Pattern PATTERN;
	
	private static Pattern getMarkdownPattern()
	{
		if(!FMLEnvironment.isProduction() || PATTERN == null)
		{
			PATTERN = Pattern.compile(
					"""
					(\\\\(?<escaped>[\\\\*_~<>\\-\\[\\]]))\
					|(?<checkbox>^(?<checkboxspaces>\\s*)[-*]\\s\\[(?<checkboxfilled>[\\sxX])]\\s)\
					|(?<bulletpoint>^(?<bulletpointspaces>\\s*)[-*]\\s)\
					|(?<!\\*)\\*\\*\\*(?<bolditalic>[\\s\\S]+?)\\*\\*\\*(?!\\*)\
					|(?<!\\*)\\*\\*(?<bold>[\\s\\S]+?)\\*\\*(?!\\*)\
					|(?<!\\*)\\*(?<italic>[\\s\\S]+?)\\*(?!\\*)\
					|(?<!_)__(?<underline>[\\s\\S]+?)__(?!_)\
					|(?<!~)~~(?<strikethrough>[\\s\\S]+?)~~(?!~)\
					|(?<placeholder><(?<placeholderkey>[^>]+)>)\
					""",
					Pattern.MULTILINE
			);
		}
		return PATTERN;
	}
	
	public static MutableComponent parse(String text)
	{
		var result = Component.empty();
		
		var matcher = getMarkdownPattern().matcher(text);
		int lastEndIndex = 0;
		
		while(matcher.find())
		{
			if(matcher.start() > lastEndIndex)
			{
				result = result.append(Component.literal(text.substring(lastEndIndex, matcher.start())));
			}
			
			boolean appended = appendEscaped(matcher, result) ||
							   appendLineItems(matcher, result) ||
							   appendStyle(matcher, result) ||
							   appendPlaceholder(matcher, result);
			
			lastEndIndex = matcher.end();
		}
		
		if(lastEndIndex < text.length())
		{
			result = result.append(Component.literal(text.substring(lastEndIndex)));
		}
		
		return result;
	}
	
	private static boolean appendEscaped(Matcher matcher, MutableComponent result)
	{
		String matchedText = matcher.group("escaped");
		if(matchedText != null)
		{
			result.append(Component.literal(matchedText));
			return true;
		}
		return false;
	}
	
	private static boolean appendStyle(Matcher matcher, MutableComponent result)
	{
		Style style = Style.EMPTY;
		String matchedText;
		if((matchedText = matcher.group("bolditalic")) != null)
		{
			style = style.withBold(true).withItalic(true);
		}
		else if((matchedText = matcher.group("bold")) != null)
		{
			style = style.withBold(true);
		}
		else if((matchedText = matcher.group("italic")) != null)
		{
			style = style.withItalic(true);
		}
		else if((matchedText = matcher.group("underline")) != null)
		{
			style = style.withUnderlined(true);
		}
		else if((matchedText = matcher.group("strikethrough")) != null)
		{
			style = style.withStrikethrough(true);
		}
		
		if(matchedText != null)
		{
			result.append(parse(matchedText).withStyle(style));
			return true;
		}
		return false;
	}
	
	private static boolean appendPlaceholder(Matcher matcher, MutableComponent result)
	{
		String matchedText;
		if((matchedText = matcher.group("placeholder")) != null)
		{
			var placeholderKey = matcher.group("placeholderkey");
			if(LogicTypes.exists(placeholderKey))
			{
				var logicType = LogicTypes.get(placeholderKey);
				result.append(logicType.displaySymbol());
			}
			else
			{
				result
						.append(Component.literal(Character.toString(matchedText.charAt(0))))
						.append(parse(matchedText.substring(1)));
			}
			return true;
		}
		return false;
	}
	
	private static boolean appendLineItems(Matcher matcher, MutableComponent result)
	{
		if(matcher.group("checkbox") != null)
		{
			var spaces = matcher.group("checkboxspaces");
			boolean filled = !matcher.group("checkboxfilled").matches("\\s");
			result.append(Component.literal(spaces + "1" + (filled ? "x" : "o") + "1 ").withStyle(Style.EMPTY.withFont(LBRFonts.STICKY_NOTE)));
			return true;
		}
		else if(matcher.group("bulletpoint") != null)
		{
			var spaces = matcher.group("bulletpointspaces");
			result.append(Component.literal(spaces + "11-11 ").withStyle(Style.EMPTY.withFont(LBRFonts.STICKY_NOTE)));
			return true;
		}
		return false;
	}
	
	public static final StickyNote EMPTY = new StickyNote("");
	
	public static final Codec<StickyNote> CODEC = Codec.STRING.xmap(StickyNote::new, StickyNote::text);
	
	public static final StreamCodec<ByteBuf, StickyNote> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(StickyNote::new, StickyNote::text);
	
	private final String text;
	
	private Component parsed;
	
	public StickyNote(String text)
	{
		this.text = text;
	}
	
	public String text()
	{
		return text;
	}
	
	public boolean isEmpty()
	{
		return text.isEmpty();
	}
	
	public Component parsed()
	{
		if(parsed == null)
		{
			parsed = parse(text);
		}
		return parsed;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o == this)
		{
			return true;
		}
		if(o == null || o.getClass() != this.getClass())
		{
			return false;
		}
		var other = (StickyNote) o;
		return Objects.equals(text, other.text);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(text);
	}
}
