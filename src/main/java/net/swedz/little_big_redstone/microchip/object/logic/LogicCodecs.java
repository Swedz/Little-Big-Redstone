package net.swedz.little_big_redstone.microchip.object.logic;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;

public final class LogicCodecs
{
	public static final Codec<LogicComponent>                COMPONENT_CODEC        = LogicTypes.COMPONENT_CODEC;
	public static final StreamCodec<ByteBuf, LogicComponent> COMPONENT_STREAM_CODEC = LogicTypes.COMPONENT_STREAM_CODEC;
	
	public static final Codec<LogicConfig>                CONFIG_CODEC        = LogicTypes.CONFIG_CODEC;
	public static final StreamCodec<ByteBuf, LogicConfig> CONFIG_STREAM_CODEC = LogicTypes.CONFIG_STREAM_CODEC;
}
