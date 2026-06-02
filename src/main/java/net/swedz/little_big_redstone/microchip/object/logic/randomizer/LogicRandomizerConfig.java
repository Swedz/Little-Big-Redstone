package net.swedz.little_big_redstone.microchip.object.logic.randomizer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;
import net.swedz.tesseract.neoforge.api.range.IntRange;

import java.util.List;

public record LogicRandomizerConfig(
		int outputs,
		float chance
) implements LogicConfig<LogicRandomizerConfig>
{
	public static final LogicRandomizerConfig DEFAULT = new LogicRandomizerConfig(
			1,
			1
	);
	
	public static final MapCodec<LogicRandomizerConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.intRange(1, 10).optionalFieldOf("outputs", 1).forGetter(LogicRandomizerConfig::outputs),
					Codec.FLOAT.optionalFieldOf("chance", 1f).forGetter(LogicRandomizerConfig::chance)
			)
			.apply(instance, LogicRandomizerConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicRandomizerConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, LogicRandomizerConfig::outputs,
			ByteBufCodecs.FLOAT, LogicRandomizerConfig::chance,
			LogicRandomizerConfig::new
	);
	
	@Override
	public LogicType<?, LogicRandomizerConfig> type()
	{
		return LogicTypes.RANDOMIZER;
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpRandomizer());
	}
	
	@Override
	public void appendConfigHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicConfigTooltipOutputs(outputs));
		lines.add(LBR.text().logicConfigTooltipChance(chance));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public LogicConfigMenuProvider getMenuProvider()
	{
		return new LogicRandomizerConfigMenuProvider(this);
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public int inputs()
	{
		return 1;
	}
	
	@Override
	public IntRange outputsAllowed()
	{
		return new IntRange(1, 10);
	}
	
	@Override
	public int outputs()
	{
		return outputs;
	}
}