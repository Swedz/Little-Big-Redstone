package net.swedz.little_big_redstone.microchip.wire;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record Wire(WirePort output, WirePort input)
{
	public static final Codec<Wire> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					WirePort.CODEC.fieldOf("output").forGetter(Wire::output),
					WirePort.CODEC.fieldOf("input").forGetter(Wire::input)
			)
			.apply(instance, Wire::new));
	
	public static final StreamCodec<ByteBuf, Wire> STREAM_CODEC = StreamCodec.composite(
			WirePort.STREAM_CODEC, Wire::output,
			WirePort.STREAM_CODEC, Wire::output,
			Wire::new
	);
	
	public Wire(int outputSlot, int outputPort, int inputSlot, int inputPort)
	{
		this(new WirePort(outputSlot, outputPort), new WirePort(inputSlot, inputPort));
	}
}
