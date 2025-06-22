package net.swedz.little_big_redstone.microchip.object.logic.randomizer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfigMenuBuilder;
import net.swedz.tesseract.neoforge.api.range.IntRange;
import net.swedz.tesseract.neoforge.tooltip.Parser;

import java.util.List;
import java.util.Objects;

import static net.swedz.little_big_redstone.LBRTextLine.*;
import static net.swedz.little_big_redstone.LBRTooltips.*;

public final class LogicRandomizerConfig extends LogicConfig<LogicRandomizerConfig>
{
	public static final MapCodec<LogicRandomizerConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.intRange(1, 10).optionalFieldOf("outputs", 1).forGetter((config) -> config.outputs),
					Codec.FLOAT.optionalFieldOf("chance", 1f).forGetter((config) -> config.chance)
			)
			.apply(instance, LogicRandomizerConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicRandomizerConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, (config) -> config.outputs,
			ByteBufCodecs.FLOAT, (config) -> config.chance,
			LogicRandomizerConfig::new
	);
	
	public int outputs;
	
	public float chance;
	
	private LogicRandomizerConfig(int outputs, float chance)
	{
		this.outputs = outputs;
		this.chance = chance;
	}
	
	public LogicRandomizerConfig()
	{
		this(1, 1);
	}
	
	@Override
	public void appendHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_OUTPUTS).arg(outputs));
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_CHANCE).arg(chance, 0, Parser.FLOAT_PERCENTAGE.withStyle(HIGHLIGHT_STYLE)));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public void buildMenu(LogicConfigMenuBuilder builder)
	{
		builder.addSlider(LBRText.LOGIC_CONFIG_BUTTON_LABEL_OUTPUTS.text(), Component.empty(), LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_OUTPUTS.text(), 0, 0, 160, 18, this.outputsAllowed().min(), this.outputsAllowed().max(), outputs, 1, 0, (value) -> outputs = value.intValue());
		
		builder.addSlider(LBRText.LOGIC_CONFIG_BUTTON_LABEL_CHANCE.text(), Component.literal("%"), LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_RANDOMIZER_CHANCE.text(), 0, 23, 160, 18, 1, 100, chance * 100, 1, 0, (value) -> chance = (float) (value / 100f));
	}
	
	@Override
	protected void internalLoadFrom(LogicRandomizerConfig other)
	{
		outputs = other.outputs;
		chance = other.chance;
	}
	
	@Override
	public void resetForPickup()
	{
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
	
	@Override
	public int hashCode()
	{
		return Objects.hash(outputs, chance);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicRandomizerConfig other && outputs == other.outputs && chance == other.chance);
	}
}
