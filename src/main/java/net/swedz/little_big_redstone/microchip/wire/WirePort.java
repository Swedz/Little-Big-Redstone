package net.swedz.little_big_redstone.microchip.wire;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record WirePort(int slot, int index) implements PortReference
{
	public static final Codec<WirePort> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.INT.fieldOf("slot").forGetter(WirePort::slot),
					Codec.INT.fieldOf("index").forGetter(WirePort::index)
			)
			.apply(instance, WirePort::new));
	
	public static final StreamCodec<ByteBuf, WirePort> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, WirePort::slot,
			ByteBufCodecs.VAR_INT, WirePort::index,
			WirePort::new
	);
	
	public WirePort(PortReference port)
	{
		this(port.slot(), port.index());
	}
}
