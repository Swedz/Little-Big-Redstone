package net.swedz.little_big_redstone.microchip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.logic.Logic;

public record LogicIndex(int slot, Logic logic, LogicOutputPorts outputPorts)
{
	public static final Codec<LogicIndex> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.INT.fieldOf("slot").forGetter(LogicIndex::slot),
					Logic.CODEC.fieldOf("logic").forGetter(LogicIndex::logic),
					LogicOutputPorts.CODEC.fieldOf("output_ports").forGetter(LogicIndex::outputPorts)
			)
			.apply(instance, LogicIndex::new));
	
	public static final StreamCodec<ByteBuf, LogicIndex> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, LogicIndex::slot,
			Logic.STREAM_CODEC, LogicIndex::logic,
			LogicOutputPorts.STREAM_CODEC, LogicIndex::outputPorts,
			LogicIndex::new
	);
}
