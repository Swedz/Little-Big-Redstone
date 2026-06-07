package net.swedz.little_big_redstone.microchip.object.logic.latch.tflipflop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTickingContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;

import java.util.Objects;
import java.util.Optional;

public final class TFlipFlop extends LogicComponent<TFlipFlop, TFlipFlopConfig>
{
	public static final MapCodec<TFlipFlop> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					DyeColor.CODEC.optionalFieldOf("color").forGetter(TFlipFlop::color),
					Codec.BOOL.optionalFieldOf("last_input", false).forGetter(TFlipFlop::lastInput),
					Codec.INT.optionalFieldOf("output", 0).forGetter(TFlipFlop::output)
			)
			.apply(instance, TFlipFlop::new));
	
	public static final StreamCodec<ByteBuf, TFlipFlop> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), TFlipFlop::color,
			ByteBufCodecs.BOOL, TFlipFlop::lastInput,
			ByteBufCodecs.VAR_INT, TFlipFlop::output,
			TFlipFlop::new
	);
	
	private boolean lastInputState;
	private int     outputState;
	
	private TFlipFlop(Optional<DyeColor> color, boolean lastInputState, int outputState)
	{
		super(color);
		this.lastInputState = lastInputState;
		this.outputState = outputState;
	}
	
	public TFlipFlop()
	{
		this(Optional.empty(), false, 0);
	}
	
	@Override
	public LogicType<TFlipFlop, TFlipFlopConfig> type()
	{
		return LBRLogicTypes.T_FLIP_FLOP.get();
	}
	
	@Override
	protected void processTickInternal(LogicTickingContext context, int[] inputs)
	{
		int input = inputs[0];
		
		if(!lastInputState && input > 0)
		{
			outputState = outputState > 0 ? 0 : input;
			context.markDirty(this);
		}
		
		lastInputState = input > 0;
	}
	
	public boolean lastInput()
	{
		return lastInputState;
	}
	
	@Override
	protected int outputInternal(int index)
	{
		return outputState;
	}
	
	public int output()
	{
		return this.output(0);
	}
	
	@Override
	protected void internalLoadFrom(TFlipFlop other)
	{
		lastInputState = other.lastInputState;
		outputState = other.outputState;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.type(), color);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof TFlipFlop other && Objects.equals(color, other.color));
	}
}