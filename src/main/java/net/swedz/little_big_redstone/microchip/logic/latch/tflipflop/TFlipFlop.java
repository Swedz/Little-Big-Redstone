package net.swedz.little_big_redstone.microchip.logic.latch.tflipflop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;

import java.util.List;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public final class TFlipFlop extends LogicComponent<TFlipFlop, TFlipFlopConfig>
{
	public static final MapCodec<TFlipFlop> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.BOOL.fieldOf("last_input").forGetter(TFlipFlop::lastInput),
					Codec.BOOL.fieldOf("output").forGetter(TFlipFlop::output)
			)
			.apply(instance, TFlipFlop::new));
	
	public static final StreamCodec<ByteBuf, TFlipFlop> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, TFlipFlop::lastInput,
			ByteBufCodecs.BOOL, TFlipFlop::output,
			TFlipFlop::new
	);
	
	private boolean lastInputState, outputState;
	
	private TFlipFlop(boolean lastInputState, boolean outputState)
	{
		this.lastInputState = lastInputState;
		this.outputState = outputState;
	}
	
	public TFlipFlop()
	{
		this(false, false);
	}
	
	@Override
	protected TFlipFlopConfig defaultConfig()
	{
		return TFlipFlopConfig.INSTANCE;
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
		}
		
		lastInputState = input;
	}
	
	public boolean lastInput()
	{
		return lastInputState;
	}
	
	@Override
	public boolean output(int index)
	{
		return outputState;
	}
	
	public boolean output()
	{
		return outputState;
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_HELP_T_FLIP_FLOP));
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
	public TFlipFlop copy()
	{
		return new TFlipFlop(lastInputState, outputState);
	}
	
	@Override
	public int hashCode()
	{
		return this.type().hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof TFlipFlop;
	}
}
