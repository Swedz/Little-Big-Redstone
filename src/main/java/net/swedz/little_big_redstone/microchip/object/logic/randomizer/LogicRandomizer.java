package net.swedz.little_big_redstone.microchip.object.logic.randomizer;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTickingContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;

import java.util.Optional;

public final class LogicRandomizer extends LogicComponent<LogicRandomizer, LogicRandomizerConfig>
{
	private static final RandomSource RANDOM = RandomSource.create();
	
	public static final MapCodec<LogicRandomizer> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					LogicRandomizerConfig.CODEC.fieldOf("config").forGetter(LogicRandomizer::config),
					DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicRandomizer::color),
					Codec.intRange(-1, 9).optionalFieldOf("outputIndex", -1).forGetter(LogicRandomizer::outputIndex),
					Codec.INT.optionalFieldOf("output", 0).forGetter(LogicRandomizer::output)
			)
			.apply(instance, LogicRandomizer::new));
	
	public static final StreamCodec<ByteBuf, LogicRandomizer> STREAM_CODEC = StreamCodec.composite(
			LogicRandomizerConfig.STREAM_CODEC, LogicRandomizer::config,
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicRandomizer::color,
			ByteBufCodecs.VAR_INT, LogicRandomizer::outputIndex,
			ByteBufCodecs.VAR_INT, LogicRandomizer::output,
			LogicRandomizer::new
	);
	
	private int outputIndex, outputState;
	
	private LogicRandomizer(LogicRandomizerConfig config, Optional<DyeColor> color, int outputIndex, int outputState)
	{
		super(config, color);
		this.outputIndex = outputIndex;
		this.outputState = outputState;
	}
	
	private LogicRandomizer(Optional<DyeColor> color, int outputIndex, int outputState)
	{
		super(color);
		this.outputIndex = outputIndex;
		this.outputState = outputState;
	}
	
	public LogicRandomizer()
	{
		this(Optional.empty(), -1, 0);
	}
	
	public int outputIndex()
	{
		return outputIndex;
	}
	
	@Override
	protected void processTickInternal(LogicTickingContext context, int[] inputs)
	{
		int originalOutputIndex = outputIndex;
		
		int input = inputs[0];
		if(input > 0 && RANDOM.nextFloat() <= config.chance())
		{
			outputIndex = RANDOM.nextInt(config.outputs());
			outputState = input;
		}
		else
		{
			outputIndex = -1;
			outputState = 0;
		}
		
		if(originalOutputIndex != outputIndex)
		{
			context.markDirty(this);
		}
	}
	
	@Override
	protected int outputInternal(int index)
	{
		return index == outputIndex ? outputState : 0;
	}
	
	public int output()
	{
		return outputState;
	}
	
	@Override
	protected void internalLoadFrom(LogicRandomizer other)
	{
		outputIndex = other.outputIndex;
		outputState = other.outputState;
	}
	
	@Override
	public LogicType<LogicRandomizer, LogicRandomizerConfig> type()
	{
		return LBRLogicTypes.RANDOMIZER.get();
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