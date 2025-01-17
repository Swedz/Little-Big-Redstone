package net.swedz.redstone_circuitry.microchip.logic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.redstone_circuitry.microchip.logic.gate.ANDGate;
import net.swedz.redstone_circuitry.microchip.logic.gate.LogicGate;
import net.swedz.redstone_circuitry.microchip.logic.gate.NANDGate;
import net.swedz.redstone_circuitry.microchip.logic.gate.NORGate;
import net.swedz.redstone_circuitry.microchip.logic.gate.NOTGate;
import net.swedz.redstone_circuitry.microchip.logic.gate.ORGate;
import net.swedz.redstone_circuitry.microchip.logic.gate.XORGate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class Logics
{
	private static final List<LogicType<?>>        LOGICS     = Lists.newArrayList();
	private static final Map<String, LogicType<?>> LOGICS_MAP = Maps.newHashMap();
	
	public static final Codec<Logic> CODEC = Codec.STRING
			.comapFlatMap(Logics::getMaybe, LogicType::id)
			.dispatch(Logic::type, LogicType::codec);
	
	public static final StreamCodec<ByteBuf, Logic> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
			.map(Logics::get, LogicType::id)
			.dispatch(Logic::type, LogicType::streamCodec);
	
	public static List<LogicType<?>> values()
	{
		return Collections.unmodifiableList(LOGICS);
	}
	
	private static DataResult<LogicType<?>> getMaybe(String id)
	{
		var type = LOGICS_MAP.get(id);
		return type == null ? DataResult.error(() -> "No logic gizmo exists for the id %s".formatted(id)) : DataResult.success(type);
	}
	
	public static LogicType<?> get(String id)
	{
		return getMaybe(id).getOrThrow(IllegalArgumentException::new);
	}
	
	private static <T extends LogicGate> LogicType<T> register(
			String id, String englishName,
			MapCodec<T> codec, StreamCodec<ByteBuf, T> streamCodec,
			LogicFactory defaultFactory
	)
	{
		var type = new LogicType<>(id, englishName, codec, streamCodec, defaultFactory);
		LOGICS.add(type);
		LOGICS_MAP.put(id, type);
		return type;
	}
	
	public static final LogicType<NOTGate>  NOT  = register("not", "NOT", NOTGate.CODEC, NOTGate.STREAM_CODEC, () -> NOTGate.INSTANCE);
	public static final LogicType<ANDGate>  AND  = register("and", "AND", ANDGate.CODEC, ANDGate.STREAM_CODEC, () -> ANDGate.INSTANCE);
	public static final LogicType<NANDGate> NAND = register("nand", "NAND", NANDGate.CODEC, NANDGate.STREAM_CODEC, () -> NANDGate.INSTANCE);
	public static final LogicType<ORGate>   OR   = register("or", "OR", ORGate.CODEC, ORGate.STREAM_CODEC, () -> ORGate.INSTANCE);
	public static final LogicType<NORGate>  NOR  = register("nor", "NOR", NORGate.CODEC, NORGate.STREAM_CODEC, () -> NORGate.INSTANCE);
	public static final LogicType<XORGate>  XOR  = register("xor", "XOR", XORGate.CODEC, XORGate.STREAM_CODEC, () -> XORGate.INSTANCE);
	
	public static void init()
	{
	}
}
