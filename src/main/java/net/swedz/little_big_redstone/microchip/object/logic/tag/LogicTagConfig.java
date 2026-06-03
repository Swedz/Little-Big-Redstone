package net.swedz.little_big_redstone.microchip.object.logic.tag;

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

public record LogicTagConfig(
		boolean input,
		LogicTagLabel label,
		int threshold,
		boolean global
) implements LogicConfig<LogicTagConfig>
{
	public static final LogicTagConfig DEFAULT = new LogicTagConfig(
			true,
			LogicTagLabel.EMPTY,
			1,
			false
	);
	
	public static final MapCodec<LogicTagConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.BOOL.optionalFieldOf("input", true).forGetter(LogicTagConfig::input),
					LogicTagLabel.CODEC.optionalFieldOf("label", LogicTagLabel.EMPTY).forGetter(LogicTagConfig::label),
					Codec.intRange(1, 100).optionalFieldOf("threshold", 1).forGetter(LogicTagConfig::threshold),
					Codec.BOOL.optionalFieldOf("global", false).forGetter(LogicTagConfig::global)
			)
			.apply(instance, LogicTagConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicTagConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, LogicTagConfig::input,
			LogicTagLabel.STREAM_CODEC, LogicTagConfig::label,
			ByteBufCodecs.INT, LogicTagConfig::threshold,
			ByteBufCodecs.BOOL, LogicTagConfig::global,
			LogicTagConfig::new
	);
	
	@Override
	public LogicType<?, LogicTagConfig> type()
	{
		return LogicTypes.TAG;
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
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpTag1());
		lines.add(LBR.text().logicHelpTag2());
		lines.add(LBR.text().logicHelpTag3());
	}
	
	@Override
	public void appendConfigHoverText(List<Component> lines)
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
}