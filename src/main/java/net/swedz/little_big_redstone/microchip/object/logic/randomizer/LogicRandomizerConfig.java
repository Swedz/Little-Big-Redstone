package net.swedz.little_big_redstone.microchip.object.logic.randomizer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfigMenuBuilder;
import net.swedz.tesseract.neoforge.api.range.IntRange;

import java.util.List;
import java.util.Objects;

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
		lines.add(LBR.text().logicConfigTooltipOutputs(outputs));
		lines.add(LBR.text().logicConfigTooltipChance(chance));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public void buildMenu(LogicConfigMenuBuilder builder, int width, int height)
	{
		builder.addSlider(LBR.text().logicConfigButtonLabelOutputs(), Component.empty(), LBR.text().logicConfigButtonTooltipOutputs(), 0, 0, width, 18, this.outputsAllowed().min(), this.outputsAllowed().max(), outputs, 1, 0, (value) -> outputs = value.intValue());
		
		builder.addSlider(LBR.text().logicConfigButtonLabelChance(), Component.literal("%"), LBR.text().logicConfigButtonTooltipRandomizerChance(), 0, 22, width, 18, 1, 100, chance * 100, 1, 0, (value) -> chance = (float) (value / 100f));
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