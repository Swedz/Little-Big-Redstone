package net.swedz.redstone_circuitry.microchip.gate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class LogicGates
{
	private static final List<LogicGateType<?>>        LOGIC_GATES     = Lists.newArrayList();
	private static final Map<String, LogicGateType<?>> LOGIC_GATES_MAP = Maps.newHashMap();
	
	public static final Codec<LogicGate> CODEC = Codec.STRING
			.comapFlatMap(LogicGates::getMaybe, LogicGateType::id)
			.dispatch(LogicGate::type, LogicGateType::codec);
	
	public static final StreamCodec<ByteBuf, LogicGate> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
			.map(LogicGates::get, LogicGateType::id)
			.dispatch(LogicGate::type, LogicGateType::streamCodec);
	
	public static List<LogicGateType<?>> values()
	{
		return Collections.unmodifiableList(LOGIC_GATES);
	}
	
	private static DataResult<LogicGateType<?>> getMaybe(String id)
	{
		var type = LOGIC_GATES_MAP.get(id);
		return type == null ? DataResult.error(() -> "No logic gate exists for the id %s".formatted(id)) : DataResult.success(type);
	}
	
	public static LogicGateType<?> get(String id)
	{
		return getMaybe(id).getOrThrow(IllegalArgumentException::new);
	}
	
	private static <T extends LogicGate> LogicGateType<T> register(String id, String englishName, MapCodec<T> codec, StreamCodec<ByteBuf, T> streamCodec, LogicGateFactory defaultFactory)
	{
		var type = new LogicGateType<>(id, englishName, codec, streamCodec, defaultFactory);
		LOGIC_GATES.add(type);
		LOGIC_GATES_MAP.put(id, type);
		return type;
	}
	
	public static final LogicGateType<NOTGate>  NOT  = register("not", "NOT", NOTGate.CODEC, NOTGate.STREAM_CODEC, () -> NOTGate.INSTANCE);
	public static final LogicGateType<ANDGate>  AND  = register("and", "AND", ANDGate.CODEC, ANDGate.STREAM_CODEC, () -> ANDGate.INSTANCE);
	public static final LogicGateType<NANDGate> NAND = register("nand", "NAND", NANDGate.CODEC, NANDGate.STREAM_CODEC, () -> NANDGate.INSTANCE);
	public static final LogicGateType<ORGate>   OR   = register("or", "OR", ORGate.CODEC, ORGate.STREAM_CODEC, () -> ORGate.INSTANCE);
	public static final LogicGateType<NORGate>  NOR  = register("nor", "NOR", NORGate.CODEC, NORGate.STREAM_CODEC, () -> NORGate.INSTANCE);
	
	public static void init()
	{
	}
}
