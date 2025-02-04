package net.swedz.little_big_redstone.microchip.logic.toggle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;

public final class ToggleGate extends LogicComponent<ToggleGate, ToggleGateConfig>
{
	public static final MapCodec<ToggleGate> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.BOOL.fieldOf("last_input").forGetter(ToggleGate::lastInput),
					Codec.BOOL.fieldOf("output").forGetter(ToggleGate::output)
			)
			.apply(instance, ToggleGate::new));
	
	public static final StreamCodec<ByteBuf, ToggleGate> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, ToggleGate::lastInput,
			ByteBufCodecs.BOOL, ToggleGate::output,
			ToggleGate::new
	);
	
	private boolean lastInputState, outputState;
	
	private ToggleGate(boolean lastInputState, boolean outputState)
	{
		this.lastInputState = lastInputState;
		this.outputState = outputState;
	}
	
	public ToggleGate()
	{
		this(false, false);
	}
	
	@Override
	protected ToggleGateConfig defaultConfig()
	{
		return ToggleGateConfig.INSTANCE;
	}
	
	@Override
	public LogicType<ToggleGate> type()
	{
		return LogicTypes.TOGGLE;
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
	protected void internalLoadFrom(ToggleGate other)
	{
		outputState = other.output();
	}
	
	@Override
	public void internalResetForPickup()
	{
		outputState = false;
	}
	
	@Override
	public ToggleGate copy()
	{
		return new ToggleGate(lastInputState, outputState);
	}
	
	@Override
	public int hashCode()
	{
		return this.type().hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof ToggleGate;
	}
}
