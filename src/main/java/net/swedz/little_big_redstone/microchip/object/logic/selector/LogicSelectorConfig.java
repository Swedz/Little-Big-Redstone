package net.swedz.little_big_redstone.microchip.object.logic.selector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;
import net.swedz.tesseract.neoforge.api.range.IntRange;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.List;
import java.util.Objects;

public final class LogicSelectorConfig extends LogicConfig<LogicSelectorConfig>
{
	public static final MapCodec<LogicSelectorConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					CodecHelper.forLowercaseEnum(LogicSelectorMode.class).optionalFieldOf("mode", LogicSelectorMode.COUNTER).forGetter((config) -> config.mode),
					Codec.intRange(2, 10).optionalFieldOf("outputs", 2).forGetter((config) -> config.outputs),
					Codec.BOOL.optionalFieldOf("pass_signal", true).forGetter((config) -> config.passSignal)
			)
			.apply(instance, LogicSelectorConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicSelectorConfig> STREAM_CODEC = StreamCodec.composite(
			CodecHelper.forLowercaseEnumStream(LogicSelectorMode.class), (config) -> config.mode,
			ByteBufCodecs.INT, (config) -> config.outputs,
			ByteBufCodecs.BOOL, (config) -> config.passSignal,
			LogicSelectorConfig::new
	);
	
	public LogicSelectorMode mode;
	
	public int outputs;
	
	public boolean passSignal;
	
	private LogicSelectorConfig(LogicSelectorMode mode, int outputs, boolean passSignal)
	{
		this.mode = mode;
		this.outputs = outputs;
		this.passSignal = passSignal;
	}
	
	public LogicSelectorConfig()
	{
		this(LogicSelectorMode.COUNTER, 2, true);
	}
	
	@Override
	public void appendHoverText(List<Component> lines)
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
	protected void internalLoadFrom(LogicSelectorConfig other)
	{
		mode = other.mode;
		outputs = other.outputs;
		passSignal = other.passSignal;
	}
	
	@Override
	public void resetForPickup()
	{
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(2, 10);
	}
	
	@Override
	public int inputs()
	{
		return mode == LogicSelectorMode.COUNTER ? 2 : outputs;
	}
	
	@Override
	public IntRange outputsAllowed()
	{
		return new IntRange(2, 10);
	}
	
	@Override
	public int outputs()
	{
		return outputs;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(mode, outputs, passSignal);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicSelectorConfig other && mode == other.mode && outputs == other.outputs && passSignal == other.passSignal);
	}
}