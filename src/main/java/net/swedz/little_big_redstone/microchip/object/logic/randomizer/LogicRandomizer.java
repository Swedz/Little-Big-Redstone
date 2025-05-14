package net.swedz.little_big_redstone.microchip.object.logic.randomizer;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicGridSize;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

import java.util.List;
import java.util.Optional;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public final class LogicRandomizer extends LogicComponent<LogicRandomizer, LogicRandomizerConfig>
{
	public static final MapCodec<LogicRandomizer> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					LogicRandomizerConfig.CODEC.fieldOf("config").forGetter(LogicRandomizer::config),
					DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicRandomizer::color),
					Codec.intRange(-1, 7).optionalFieldOf("outputIndex", -1).forGetter(LogicRandomizer::outputIndex)
			)
			.apply(instance, LogicRandomizer::new));
	
	public static final StreamCodec<ByteBuf, LogicRandomizer> STREAM_CODEC = StreamCodec.composite(
			LogicRandomizerConfig.STREAM_CODEC, LogicRandomizer::config,
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicRandomizer::color,
			ByteBufCodecs.INT, LogicRandomizer::outputIndex,
			LogicRandomizer::new
	);
	
	private int outputIndex;
	
	private LogicRandomizer(LogicRandomizerConfig config, Optional<DyeColor> color, int outputIndex)
	{
		super(config, color);
		this.outputIndex = outputIndex;
	}
	
	private LogicRandomizer(Optional<DyeColor> color, int outputIndex)
	{
		super(color);
		this.outputIndex = outputIndex;
	}
	
	public LogicRandomizer()
	{
		this(Optional.empty(), -1);
	}
	
	public int outputIndex()
	{
		return outputIndex;
	}
	
	@Override
	protected void processTickInternal(LogicContext context, boolean[] inputs)
	{
		int originalOutputIndex = outputIndex;
		
		if(inputs[0] && context.level().random.nextFloat() <= config.chance)
		{
			outputIndex = context.level().random.nextInt(config.outputs);
		}
		else
		{
			outputIndex = -1;
		}
		
		if(originalOutputIndex != outputIndex)
		{
			context.markDirty(this);
		}
	}
	
	@Override
	public boolean output(int index)
	{
		return index == outputIndex;
	}
	
	@Override
	public LogicGridSize size()
	{
		int outputs = this.outputs();
		return new LogicGridSize(1, Math.max(1, outputs / 2));
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_HELP_RANDOMIZER));
	}
	
	@Override
	protected void internalLoadFrom(LogicRandomizer other)
	{
		outputIndex = other.outputIndex;
	}
	
	@Override
	protected void internalResetForPickup()
	{
		outputIndex = -1;
	}
	
	@Override
	public LogicRandomizer copy()
	{
		return new LogicRandomizer(config.copy(), color, outputIndex);
	}
	
	@Override
	protected LogicRandomizerConfig defaultConfig()
	{
		return new LogicRandomizerConfig();
	}
	
	@Override
	public LogicType<LogicRandomizer> type()
	{
		return LogicTypes.RANDOMIZER;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.type(), config, color, outputIndex);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicRandomizer other && Objects.equal(config, other.config) && Objects.equal(color, other.color) && outputIndex == other.outputIndex);
	}
}
