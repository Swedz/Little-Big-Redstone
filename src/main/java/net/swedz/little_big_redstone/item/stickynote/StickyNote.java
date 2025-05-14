package net.swedz.little_big_redstone.item.stickynote;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.Objects;

public final class StickyNote
{
	public static final StickyNote EMPTY = new StickyNote("");
	
	public static final Codec<StickyNote> CODEC = Codec.STRING.xmap(StickyNote::new, StickyNote::text);
	
	public static final StreamCodec<ByteBuf, StickyNote> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(StickyNote::new, StickyNote::text);
	
	private static final MiniMessage PARSER;
	
	static
	{
		var proxy = Proxies.get(LBRProxy.class);
		
		var builder = MiniMessage.builder();
		
		var tags = TagResolver.builder();
		tags.resolver(StandardTags.decorations());
		for(var type : LogicTypes.values())
		{
			tags.resolver(Placeholder.component(type.id(), proxy.nativeToAdventure(type.displaySymbol())));
		}
		builder.tags(tags.build());
		
		builder.preProcessor(StickyNote::preProcess);
		PARSER = builder.build();
	}
	
	private static String preProcess(String string)
	{
		return string
				.replaceAll("\\*\\*\\*(.+?)\\*\\*\\*", "<i><b>$1</b></i>")
				.replaceAll("\\*\\*(.+?)\\*\\*", "<b>$1</b>")
				.replaceAll("\\*(.+?)\\*", "<i>$1</i>")
				.replaceAll("~~(.+?)~~", "<st>$1</st>")
				.replaceAll("__(.+?)__", "<u>$1</u>");
	}
	
	public static Component parse(String text)
	{
		return Proxies.get(LBRProxy.class).adventureToNative(PARSER.deserialize(text));
	}
	
	private final String    text;
	private final Component parsed;
	
	public StickyNote(String text)
	{
		this.text = text;
		this.parsed = parse(text);
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
