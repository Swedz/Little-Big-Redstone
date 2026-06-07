package net.swedz.little_big_redstone.microchip.object.logic.calculator;

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

public record LogicCalculatorConfig(
		LogicCalculatorMode mode,
		int inputs
) implements LogicConfig
{
	public static final LogicCalculatorConfig DEFAULT = new LogicCalculatorConfig(
			LogicCalculatorMode.ADDITION,
			2
	);
	
	public static final MapCodec<LogicCalculatorConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					CodecHelper.forLowercaseEnum(LogicCalculatorMode.class).optionalFieldOf("mode", LogicCalculatorMode.ADDITION).forGetter(LogicCalculatorConfig::mode),
					Codec.intRange(2, 10).optionalFieldOf("inputs", 2).forGetter(LogicCalculatorConfig::inputs)
			)
			.apply(instance, LogicCalculatorConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicCalculatorConfig> STREAM_CODEC = StreamCodec.composite(
			CodecHelper.forEnumStream(LogicCalculatorMode.class), LogicCalculatorConfig::mode,
			ByteBufCodecs.VAR_INT, LogicCalculatorConfig::inputs,
			LogicCalculatorConfig::new
	);
	
	@Override
	public LogicType type()
	{
		return LBRLogicTypes.CALCULATOR.get();
	}
	
	@Override
	public IntRange inputPortsAllowed()
	{
		return new IntRange(2, 10);
	}
	
	@Override
	public int inputPorts()
	{
		return inputs;
	}
	
	@Override
	public IntRange outputPortsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public int outputPorts()
	{
		return 1;
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpCalculator1());
		lines.add(LBR.text().logicHelpCalculator2());
	}
	
	@Override
	public void appendConfigHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicConfigTooltipMode(mode));
		
		lines.add(LBR.text().logicConfigTooltipInputs(inputs));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public LogicConfigMenuProvider<?> getMenuProvider()
	{
		return new LogicCalculatorConfigMenuProvider(this);
	}
}
