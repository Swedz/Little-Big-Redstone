package net.swedz.little_big_redstone.microchip.logic.gate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.logic.LogicConfig;

public final class LogicGateConfig extends LogicConfig<LogicGateConfig>
{
	public static final Codec<LogicGateConfig> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.INT.fieldOf("input_count").forGetter((config) -> config.inputs)
			)
			.apply(instance, LogicGateConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicGateConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, (config) -> config.inputs,
			LogicGateConfig::new
	);
	
	public int inputs;
	
	public LogicGateConfig(int inputs)
	{
		this.inputs = inputs;
	}
	
	@Override
	public LogicGateConfig copy()
	{
		return new LogicGateConfig(inputs);
	}
}
