package net.swedz.little_big_redstone.microchip.object.logic.latch.tflipflop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class TFlipFlop extends LogicComponent<TFlipFlop, TFlipFlopConfig>
{
	public static final MapCodec<TFlipFlop> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					DyeColor.CODEC.optionalFieldOf("color").forGetter(TFlipFlop::color),
					Codec.BOOL.optionalFieldOf("last_input", false).forGetter(TFlipFlop::lastInput),
					Codec.BOOL.optionalFieldOf("output", false).forGetter(TFlipFlop::output)
			)
			.apply(instance, TFlipFlop::new));
	
	public static final StreamCodec<ByteBuf, TFlipFlop> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), TFlipFlop::color,
			ByteBufCodecs.BOOL, TFlipFlop::lastInput,
			ByteBufCodecs.BOOL, TFlipFlop::output,
			TFlipFlop::new
	);
	
	private boolean lastInputState, outputState;
	
	private TFlipFlop(Optional<DyeColor> color, boolean lastInputState, boolean outputState)
	{
		super(color);
		this.lastInputState = lastInputState;
		this.outputState = outputState;
	}
	
	public TFlipFlop()
	{
		this(Optional.empty(), false, false);
	}
	
	@Override
	protected TFlipFlopConfig defaultConfig()
	{
		return new TFlipFlopConfig();
	}
	
	@Override
	public LogicType<TFlipFlop> type()
	{
		return LogicTypes.T_FLIP_FLOP;
	}
	
	@Override
	protected void processTickInternal(LogicContext context, boolean[] inputs)
	{
		boolean input = inputs[0];
		
		if(!lastInputState && input)
		{
			outputState = !outputState;
			context.markDirty(this);
		}
		
		lastInputState = input;
	}
	
	public boolean lastInput()
	{
		return lastInputState;
	}
	
	@Override
	protected boolean outputInternal(int index)
	{
		return outputState;
	}
	
	public boolean output()
	{
		return this.output(0);
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpTFlipFlop());
	}
	
	@Override
	protected void internalLoadFrom(TFlipFlop other)
	{
		lastInputState = other.lastInputState;
		outputState = other.outputState;
	}
	
	@Override
	public void internalResetForPickup()
	{
		lastInputState = false;
		outputState = false;
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