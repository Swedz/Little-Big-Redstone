package net.swedz.little_big_redstone.item.stickynote;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;
import java.util.regex.Pattern;

public final class StickyNote
{
	private static final Pattern MARKDOWN_PATTERN = Pattern.compile(
			"(?<!\\*)\\*\\*\\*(?<bolditalic>.+?)\\*\\*\\*(?!\\*)" +
			"|(?<!\\*)\\*\\*(?<bold>.+?)\\*\\*(?!\\*)" +
			"|(?<!\\*)\\*(?<italic>.+?)\\*(?!\\*)" +
			"|(?<!_)__(?<underline>.+?)__(?!_)" +
			"|(?<!~)~~(?<strikethrough>.+?)~~(?!~)"
	);
	
	public static MutableComponent parse(String text)
	{
		var result = Component.empty();
		
		var matcher = MARKDOWN_PATTERN.matcher(text);
		int lastEndIndex = 0;
		
		while(matcher.find())
		{
			if(matcher.start() > lastEndIndex)
			{
				result = result.append(Component.literal(text.substring(lastEndIndex, matcher.start())));
			}
			
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
				result = result.append(parse(matchedText).withStyle(style));
			}
			
			lastEndIndex = matcher.end();
		}
		
		if(lastEndIndex < text.length())
		{
			result = result.append(Component.literal(text.substring(lastEndIndex)));
		}
		
		return result;
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
