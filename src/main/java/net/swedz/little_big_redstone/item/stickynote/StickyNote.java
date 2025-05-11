package net.swedz.little_big_redstone.item.stickynote;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.proxy.Proxies;

public record StickyNote(String text)
{
	public static final StickyNote EMPTY = new StickyNote("");
	
	public static final Codec<StickyNote> CODEC = Codec.STRING.xmap(StickyNote::new, StickyNote::text);
	
	public static final StreamCodec<ByteBuf, StickyNote> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(StickyNote::new, StickyNote::text);
	
	private static final MiniMessage PARSER = MiniMessage.builder()
			.tags(TagResolver.builder()
					.resolver(StandardTags.decorations())
					.build())
			.build();
	
	public static Component parse(String text)
	{
		return Proxies.get(LBRProxy.class).adventureToNative(PARSER.deserialize(text));
	}
	
	public Component parse()
	{
		return parse(text);
	}
}
