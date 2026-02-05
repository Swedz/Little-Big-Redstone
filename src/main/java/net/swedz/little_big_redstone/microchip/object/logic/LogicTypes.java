package net.swedz.little_big_redstone.microchip.object.logic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.object.logic.debug.LogicDebugger;
import net.swedz.little_big_redstone.microchip.object.logic.gate.ANDGate;
import net.swedz.little_big_redstone.microchip.object.logic.gate.LogicGate;
import net.swedz.little_big_redstone.microchip.object.logic.gate.NANDGate;
import net.swedz.little_big_redstone.microchip.object.logic.gate.NORGate;
import net.swedz.little_big_redstone.microchip.object.logic.gate.NOTGate;
import net.swedz.little_big_redstone.microchip.object.logic.gate.ORGate;
import net.swedz.little_big_redstone.microchip.object.logic.gate.XORGate;
import net.swedz.little_big_redstone.microchip.object.logic.io.LogicIO;
import net.swedz.little_big_redstone.microchip.object.logic.latch.rs.RSNORLatch;
import net.swedz.little_big_redstone.microchip.object.logic.latch.tflipflop.TFlipFlop;
import net.swedz.little_big_redstone.microchip.object.logic.pulse.PulseThrottler;
import net.swedz.little_big_redstone.microchip.object.logic.randomizer.LogicRandomizer;
import net.swedz.little_big_redstone.microchip.object.logic.reader.LogicReader;
import net.swedz.little_big_redstone.microchip.object.logic.selector.LogicSelector;
import net.swedz.little_big_redstone.microchip.object.logic.sequencer.LogicSequencer;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class LogicTypes
{
	private static final List<LogicType<?>>        LOGICS     = Lists.newArrayList();
	private static final Map<String, LogicType<?>> LOGICS_MAP = Maps.newHashMap();
	
	private static final MutableInt SYMBOL = new MutableInt();
	
	static final Codec<LogicComponent> CODEC = Codec.STRING
			.comapFlatMap(LogicTypes::getMaybe, LogicType::id)
			.dispatch(LogicComponent::type, LogicType::codec);
	
	static final StreamCodec<ByteBuf, LogicComponent> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
			.map(LogicTypes::get, LogicType::id)
			.dispatch(LogicComponent::type, LogicType::streamCodec);
	
	public static final LogicType<LogicDebugger> DEBUGGER = register("debugger", "Debugger", LogicDebugger.CODEC, LogicDebugger.STREAM_CODEC, LogicDebugger::new);
	
	public static final LogicType<LogicIO>     IO     = register("io", "I/O Port", LogicIO.CODEC, LogicIO.STREAM_CODEC, LogicIO::new);
	public static final LogicType<LogicReader> READER = register("reader", "Reader", LogicReader.CODEC, LogicReader.STREAM_CODEC, LogicReader::new);
	
	public static final LogicType<NOTGate>  NOT  = registerGate("not", "NOT", NOTGate.CODEC, NOTGate.STREAM_CODEC, NOTGate::new);
	public static final LogicType<ANDGate>  AND  = registerGate("and", "AND", ANDGate.CODEC, ANDGate.STREAM_CODEC, ANDGate::new);
	public static final LogicType<NANDGate> NAND = registerGate("nand", "NAND", NANDGate.CODEC, NANDGate.STREAM_CODEC, NANDGate::new);
	public static final LogicType<ORGate>   OR   = registerGate("or", "OR", ORGate.CODEC, ORGate.STREAM_CODEC, ORGate::new);
	public static final LogicType<NORGate>  NOR  = registerGate("nor", "NOR", NORGate.CODEC, NORGate.STREAM_CODEC, NORGate::new);
	public static final LogicType<XORGate>  XOR  = registerGate("xor", "XOR", XORGate.CODEC, XORGate.STREAM_CODEC, XORGate::new);
	
	public static final LogicType<LogicSequencer>  SEQUENCER       = register("sequencer", "Sequencer", LogicSequencer.CODEC, LogicSequencer.STREAM_CODEC, LogicSequencer::new);
	public static final LogicType<PulseThrottler>  PULSE_THROTTLER = register("pulse_throttler", "Pulse Throttler", PulseThrottler.CODEC, PulseThrottler.STREAM_CODEC, PulseThrottler::new);
	public static final LogicType<LogicSelector>   SELECTOR        = register("selector", "Selector", LogicSelector.CODEC, LogicSelector.STREAM_CODEC, LogicSelector::new);
	public static final LogicType<LogicRandomizer> RANDOMIZER      = register("randomizer", "Randomizer", LogicRandomizer.CODEC, LogicRandomizer.STREAM_CODEC, LogicRandomizer::new);
	
	public static final LogicType<TFlipFlop>  T_FLIP_FLOP  = register("t_flip_flop", "T Flip-Flop", TFlipFlop.CODEC, TFlipFlop.STREAM_CODEC, TFlipFlop::new);
	public static final LogicType<RSNORLatch> RS_NOR_LATCH = register("rs_nor_latch", "RS NOR Latch", RSNORLatch.CODEC, RSNORLatch.STREAM_CODEC, RSNORLatch::new);
	
	public static List<LogicType<?>> values()
	{
		return Collections.unmodifiableList(LOGICS);
	}
	
	private static DataResult<LogicType<?>> getMaybe(String id)
	{
		var type = LOGICS_MAP.get(id);
		return type == null ? DataResult.error(() -> "No logic type exists for the id %s".formatted(id)) : DataResult.success(type);
	}
	
	public static LogicType<?> get(String id)
	{
		return getMaybe(id).getOrThrow(IllegalArgumentException::new);
	}
	
	private static <T extends LogicComponent> LogicType<T> register(
			String id, String englishName,
			MapCodec<T> codec, StreamCodec<ByteBuf, T> streamCodec,
			LogicFactory defaultFactory
	)
	{
		var type = new LogicType<>(id, englishName, (char) (SYMBOL.getAndIncrement() + (int) '0'), codec, streamCodec, defaultFactory);
		LOGICS.add(type);
		LOGICS_MAP.put(id, type);
		return type;
	}
	
	private static <T extends LogicGate> LogicType<T> registerGate(
			String id, String englishName,
			MapCodec<T> codec, StreamCodec<ByteBuf, T> streamCodec,
			LogicFactory defaultFactory
	)
	{
		return register(id + "_gate", englishName + " Gate", codec, streamCodec, defaultFactory);
	}
	
	public static void init()
	{
	}
}
