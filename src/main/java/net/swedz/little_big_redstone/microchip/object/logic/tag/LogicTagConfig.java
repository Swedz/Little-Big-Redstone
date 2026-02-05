package net.swedz.little_big_redstone.microchip.object.logic.tag;

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

import java.util.List;
import java.util.Objects;

public final class LogicTagConfig extends LogicConfig<LogicTagConfig>
{
	public static final MapCodec<LogicTagConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.BOOL.optionalFieldOf("input", true).forGetter((config) -> config.input),
					LogicTagLabel.CODEC.optionalFieldOf("label", LogicTagLabel.EMPTY).forGetter((config) -> config.label),
					Codec.intRange(1, 100).optionalFieldOf("threshold", 1).forGetter((config) -> config.threshold),
					Codec.BOOL.optionalFieldOf("global", false).forGetter((config) -> config.global)
			)
			.apply(instance, (input, label, threshold, global) -> new LogicTagConfig(true, input, label, threshold, global)));
	
	public static final StreamCodec<ByteBuf, LogicTagConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, (config) -> config.valid,
			ByteBufCodecs.BOOL, (config) -> config.input,
			LogicTagLabel.STREAM_CODEC, (config) -> config.label,
			ByteBufCodecs.INT, (config) -> config.threshold,
			ByteBufCodecs.BOOL, (config) -> config.global,
			LogicTagConfig::new
	);
	
	public boolean input;
	
	public LogicTagLabel label;
	public int           threshold;
	public boolean       global;
	
	private LogicTagConfig(boolean valid, boolean input, LogicTagLabel label, int threshold, boolean global)
	{
		this.valid = valid;
		this.input = input;
		this.label = label;
		this.threshold = threshold;
		this.global = global;
	}
	
	public LogicTagConfig()
	{
		this(true, true, LogicTagLabel.EMPTY, 1, false);
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return input ? new IntRange(0, 0) : new IntRange(1, 1);
	}
	
	@Override
	public int inputs()
	{
		return input ? 0 : 1;
	}
	
	@Override
	public IntRange outputsAllowed()
	{
		return input ? new IntRange(1, 1) : new IntRange(0, 0);
	}
	
	@Override
	public int outputs()
	{
		return input ? 1 : 0;
	}
	
	@Override
	public void appendHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicConfigTooltipMode(input ? LogicTagMode.SENSOR : LogicTagMode.EMITTER));
		if(!label.label().isEmpty())
		{
			lines.add(LBR.text().logicConfigTooltipLabel(label.label()));
		}
		if(input)
		{
			lines.add(LBR.text().logicConfigTooltipThreshold(threshold));
			lines.add(LBR.text().logicConfigTooltipGlobal(global));
		}
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public LogicConfigMenuProvider getMenuProvider()
	{
		return new LogicTagConfigMenuProvider(this);
	}
	
	@Override
	protected void internalLoadFrom(LogicTagConfig other)
	{
		input = other.input;
		label = other.label;
		threshold = other.threshold;
		global = other.global;
	}
	
	@Override
	public void resetForPickup()
	{
		valid = true;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(input, label, threshold, global);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicTagConfig other && input == other.input && Objects.equals(label, other.label) && threshold == other.threshold && global == other.global);
	}
}