package net.swedz.little_big_redstone;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicFactory;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.calculator.LogicCalculator;
import net.swedz.little_big_redstone.microchip.object.logic.calculator.LogicCalculatorConfig;
import net.swedz.little_big_redstone.microchip.object.logic.comparator.LogicComparator;
import net.swedz.little_big_redstone.microchip.object.logic.comparator.LogicComparatorConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.debug.LogicDebugger;
import net.swedz.little_big_redstone.microchip.object.logic.debug.LogicDebuggerConfig;
import net.swedz.little_big_redstone.microchip.object.logic.gate.ANDGate;
import net.swedz.little_big_redstone.microchip.object.logic.gate.LogicGate;
import net.swedz.little_big_redstone.microchip.object.logic.gate.NANDGate;
import net.swedz.little_big_redstone.microchip.object.logic.gate.NORGate;
import net.swedz.little_big_redstone.microchip.object.logic.gate.NOTGate;
import net.swedz.little_big_redstone.microchip.object.logic.gate.ORGate;
import net.swedz.little_big_redstone.microchip.object.logic.gate.XORGate;
import net.swedz.little_big_redstone.microchip.object.logic.gate.config.ANDGateConfig;
import net.swedz.little_big_redstone.microchip.object.logic.gate.config.NANDGateConfig;
import net.swedz.little_big_redstone.microchip.object.logic.gate.config.NORGateConfig;
import net.swedz.little_big_redstone.microchip.object.logic.gate.config.NOTGateConfig;
import net.swedz.little_big_redstone.microchip.object.logic.gate.config.ORGateConfig;
import net.swedz.little_big_redstone.microchip.object.logic.gate.config.XORGateConfig;
import net.swedz.little_big_redstone.microchip.object.logic.io.LogicIO;
import net.swedz.little_big_redstone.microchip.object.logic.io.LogicIOConfig;
import net.swedz.little_big_redstone.microchip.object.logic.latch.rs.RSNORLatch;
import net.swedz.little_big_redstone.microchip.object.logic.latch.rs.RSNORLatchConfig;
import net.swedz.little_big_redstone.microchip.object.logic.latch.tflipflop.TFlipFlop;
import net.swedz.little_big_redstone.microchip.object.logic.latch.tflipflop.TFlipFlopConfig;
import net.swedz.little_big_redstone.microchip.object.logic.pulse.PulseThrottler;
import net.swedz.little_big_redstone.microchip.object.logic.pulse.PulseThrottlerConfig;
import net.swedz.little_big_redstone.microchip.object.logic.randomizer.LogicRandomizer;
import net.swedz.little_big_redstone.microchip.object.logic.randomizer.LogicRandomizerConfig;
import net.swedz.little_big_redstone.microchip.object.logic.reader.LogicReader;
import net.swedz.little_big_redstone.microchip.object.logic.reader.LogicReaderConfig;
import net.swedz.little_big_redstone.microchip.object.logic.selector.LogicSelector;
import net.swedz.little_big_redstone.microchip.object.logic.selector.LogicSelectorConfig;
import net.swedz.little_big_redstone.microchip.object.logic.sequencer.LogicSequencer;
import net.swedz.little_big_redstone.microchip.object.logic.sequencer.LogicSequencerConfig;
import net.swedz.little_big_redstone.microchip.object.logic.tag.LogicTag;
import net.swedz.little_big_redstone.microchip.object.logic.tag.LogicTagConfig;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Collections;
import java.util.Set;

public final class LBRLogicTypes
{
	private static final DeferredRegister<LogicType> REGISTRY = DeferredRegister.create(LogicTypes.REGISTRY, LBR.ID);
	
	private static final Set<LogicType> VALUES = Sets.newHashSet();
	
	private static final MutableInt SYMBOL = new MutableInt();
	
	public static final DeferredHolder<LogicType, LogicType> DEBUGGER = register("debugger", "Debugger", LogicDebugger.CODEC, LogicDebugger.STREAM_CODEC, LogicDebugger::new, LogicDebuggerConfig.CODEC, LogicDebuggerConfig.STREAM_CODEC, LogicDebuggerConfig.DEFAULT);
	
	public static final DeferredHolder<LogicType, LogicType> IO     = register("io", "I/O Port", LogicIO.CODEC, LogicIO.STREAM_CODEC, LogicIO::new, LogicIOConfig.CODEC, LogicIOConfig.STREAM_CODEC, LogicIOConfig.DEFAULT);
	public static final DeferredHolder<LogicType, LogicType> READER = register("reader", "Reader", LogicReader.CODEC, LogicReader.STREAM_CODEC, LogicReader::new, LogicReaderConfig.CODEC, LogicReaderConfig.STREAM_CODEC, LogicReaderConfig.DEFAULT);
	public static final DeferredHolder<LogicType, LogicType> TAG    = register("tag", "Tag", LogicTag.CODEC, LogicTag.STREAM_CODEC, LogicTag::new, LogicTagConfig.CODEC, LogicTagConfig.STREAM_CODEC, LogicTagConfig.DEFAULT);
	
	public static final DeferredHolder<LogicType, LogicType> NOT  = registerGate("not", "NOT", NOTGate.CODEC, NOTGate.STREAM_CODEC, NOTGate::new, NOTGateConfig.CODEC, NOTGateConfig.STREAM_CODEC, NOTGateConfig.DEFAULT);
	public static final DeferredHolder<LogicType, LogicType> AND  = registerGate("and", "AND", ANDGate.CODEC, ANDGate.STREAM_CODEC, ANDGate::new, ANDGateConfig.CODEC, ANDGateConfig.STREAM_CODEC, ANDGateConfig.DEFAULT);
	public static final DeferredHolder<LogicType, LogicType> NAND = registerGate("nand", "NAND", NANDGate.CODEC, NANDGate.STREAM_CODEC, NANDGate::new, NANDGateConfig.CODEC, NANDGateConfig.STREAM_CODEC, NANDGateConfig.DEFAULT);
	public static final DeferredHolder<LogicType, LogicType> OR   = registerGate("or", "OR", ORGate.CODEC, ORGate.STREAM_CODEC, ORGate::new, ORGateConfig.CODEC, ORGateConfig.STREAM_CODEC, ORGateConfig.DEFAULT);
	public static final DeferredHolder<LogicType, LogicType> NOR  = registerGate("nor", "NOR", NORGate.CODEC, NORGate.STREAM_CODEC, NORGate::new, NORGateConfig.CODEC, NORGateConfig.STREAM_CODEC, NORGateConfig.DEFAULT);
	public static final DeferredHolder<LogicType, LogicType> XOR  = registerGate("xor", "XOR", XORGate.CODEC, XORGate.STREAM_CODEC, XORGate::new, XORGateConfig.CODEC, XORGateConfig.STREAM_CODEC, XORGateConfig.DEFAULT);
	
	public static final DeferredHolder<LogicType, LogicType> SEQUENCER       = register("sequencer", "Sequencer", LogicSequencer.CODEC, LogicSequencer.STREAM_CODEC, LogicSequencer::new, LogicSequencerConfig.CODEC, LogicSequencerConfig.STREAM_CODEC, LogicSequencerConfig.DEFAULT);
	public static final DeferredHolder<LogicType, LogicType> PULSE_THROTTLER = register("pulse_throttler", "Pulse Throttler", PulseThrottler.CODEC, PulseThrottler.STREAM_CODEC, PulseThrottler::new, PulseThrottlerConfig.CODEC, PulseThrottlerConfig.STREAM_CODEC, PulseThrottlerConfig.DEFAULT);
	public static final DeferredHolder<LogicType, LogicType> SELECTOR        = register("selector", "Selector", LogicSelector.CODEC, LogicSelector.STREAM_CODEC, LogicSelector::new, LogicSelectorConfig.CODEC, LogicSelectorConfig.STREAM_CODEC, LogicSelectorConfig.DEFAULT);
	public static final DeferredHolder<LogicType, LogicType> RANDOMIZER      = register("randomizer", "Randomizer", LogicRandomizer.CODEC, LogicRandomizer.STREAM_CODEC, LogicRandomizer::new, LogicRandomizerConfig.CODEC, LogicRandomizerConfig.STREAM_CODEC, LogicRandomizerConfig.DEFAULT);
	public static final DeferredHolder<LogicType, LogicType> COMPARATOR      = register("comparator", "Comparator", LogicComparator.CODEC, LogicComparator.STREAM_CODEC, LogicComparator::new, LogicComparatorConfig.CODEC, LogicComparatorConfig.STREAM_CODEC, LogicComparatorConfig.DEFAULT);
	public static final DeferredHolder<LogicType, LogicType> CALCULATOR      = register("calculator", "Calculator", LogicCalculator.CODEC, LogicCalculator.STREAM_CODEC, LogicCalculator::new, LogicCalculatorConfig.CODEC, LogicCalculatorConfig.STREAM_CODEC, LogicCalculatorConfig.DEFAULT);
	public static final DeferredHolder<LogicType, LogicType> T_FLIP_FLOP     = register("t_flip_flop", "T Flip-Flop", TFlipFlop.CODEC, TFlipFlop.STREAM_CODEC, TFlipFlop::new, TFlipFlopConfig.CODEC, TFlipFlopConfig.STREAM_CODEC, TFlipFlopConfig.DEFAULT);
	public static final DeferredHolder<LogicType, LogicType> RS_NOR_LATCH    = register("rs_nor_latch", "RS NOR Latch", RSNORLatch.CODEC, RSNORLatch.STREAM_CODEC, RSNORLatch::new, RSNORLatchConfig.CODEC, RSNORLatchConfig.STREAM_CODEC, RSNORLatchConfig.DEFAULT);
	
	public static Set<LogicType> values()
	{
		return Collections.unmodifiableSet(VALUES);
	}
	
	private static <T extends LogicComponent<T, C>, C extends LogicConfig> DeferredHolder<LogicType, LogicType> register(
			String name,
			String englishName,
			MapCodec<T> codec,
			StreamCodec<ByteBuf, T> streamCodec,
			LogicFactory<T> defaultFactory,
			MapCodec<C> configCodec,
			StreamCodec<ByteBuf, C> configStreamCodec,
			C defaultConfig
	)
	{
		var id = LBR.id(name);
		var type = new LogicType(
				id,
				englishName,
				(char) (SYMBOL.getAndIncrement() + (int) '0'),
				codec,
				streamCodec,
				defaultFactory,
				configCodec,
				configStreamCodec,
				defaultConfig
		);
		VALUES.add(type);
		return REGISTRY.register(name, () -> type);
	}
	
	private static <T extends LogicGate<T, C>, C extends LogicConfig> DeferredHolder<LogicType, LogicType> registerGate(
			String id,
			String englishName,
			MapCodec<T> codec,
			StreamCodec<ByteBuf, T> streamCodec,
			LogicFactory<T> defaultFactory,
			MapCodec<C> configCodec,
			StreamCodec<ByteBuf, C> configStreamCodec,
			C defaultConfig
	)
	{
		return register(id + "_gate", englishName + " Gate", codec, streamCodec, defaultFactory, configCodec, configStreamCodec, defaultConfig);
	}
	
	public static void init(IEventBus bus)
	{
		REGISTRY.register(bus);
	}
}
