package net.swedz.little_big_redstone.microchip.object.logic.selector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;
import net.swedz.tesseract.neoforge.api.range.IntRange;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.List;

public record LogicSelectorConfig(
		LogicSelectorMode mode,
		int outputs,
		boolean passSignal
) implements LogicConfig
{
	public static final LogicSelectorConfig DEFAULT = new LogicSelectorConfig(
			LogicSelectorMode.COUNTER,
			2,
			true
	);
	
	public static final MapCodec<LogicSelectorConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					CodecHelper.forLowercaseEnum(LogicSelectorMode.class).optionalFieldOf("mode", LogicSelectorMode.COUNTER).forGetter(LogicSelectorConfig::mode),
					Codec.intRange(2, 10).optionalFieldOf("outputs", 2).forGetter(LogicSelectorConfig::outputs),
					Codec.BOOL.optionalFieldOf("pass_signal", true).forGetter(LogicSelectorConfig::passSignal)
			)
			.apply(instance, LogicSelectorConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicSelectorConfig> STREAM_CODEC = StreamCodec.composite(
			CodecHelper.forLowercaseEnumStream(LogicSelectorMode.class), LogicSelectorConfig::mode,
			ByteBufCodecs.INT, LogicSelectorConfig::outputs,
			ByteBufCodecs.BOOL, LogicSelectorConfig::passSignal,
			LogicSelectorConfig::new
	);
	
	@Override
	public LogicType type()
	{
		return LBRLogicTypes.SELECTOR.get();
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpSelector());
	}
	
	@Override
	public void appendConfigHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicConfigTooltipMode(mode));
		lines.add(LBR.text().logicConfigTooltipOutputs(outputs));
		lines.add(LBR.text().logicConfigTooltipPassSignal(passSignal));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public LogicConfigMenuProvider<LogicSelectorConfig> getMenuProvider()
	{
		return new LogicSelectorConfigMenuProvider(this);
	}
	
	@Override
	public IntRange inputPortsAllowed()
	{
		return new IntRange(2, 10);
	}
	
	@Override
	public int inputPorts()
	{
		return mode == LogicSelectorMode.COUNTER ? 2 : outputs;
	}
	
	@Override
	public IntRange outputPortsAllowed()
	{
		return new IntRange(2, 10);
	}
	
	@Override
	public int outputPorts()
	{
		return outputs;
	}
}