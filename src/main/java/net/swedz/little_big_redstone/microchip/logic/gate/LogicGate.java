package net.swedz.little_big_redstone.microchip.logic.gate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.api.IntRange;
import net.swedz.little_big_redstone.microchip.logic.Logic;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class LogicGate<G extends LogicGate> extends Logic<G, LogicGateConfig>
{
	protected static <G extends LogicGate> MapCodec<G> mapCodec(BiFunction<LogicGateConfig, Boolean, G> creator)
	{
		return RecordCodecBuilder.mapCodec((instance) -> instance
				.group(
						LogicGateConfig.CODEC.fieldOf("config").forGetter((gate) -> (LogicGateConfig) gate.config()),
						Codec.BOOL.fieldOf("output").forGetter(LogicGate::output)
				)
				.apply(instance, creator));
	}
	
	protected static <G extends LogicGate> MapCodec<G> singleInputMapCodec(Function<Boolean, G> creator)
	{
		return RecordCodecBuilder.mapCodec((instance) -> instance
				.group(
						Codec.BOOL.fieldOf("output").forGetter(LogicGate::output)
				)
				.apply(instance, creator));
	}
	
	protected static <G extends LogicGate> StreamCodec<ByteBuf, G> streamCodec(BiFunction<LogicGateConfig, Boolean, G> creator)
	{
		return StreamCodec.composite(
				LogicGateConfig.STREAM_CODEC, (gate) -> (LogicGateConfig) gate.config(),
				ByteBufCodecs.BOOL, LogicGate::output,
				creator
		);
	}
	
	protected static <G extends LogicGate> StreamCodec<ByteBuf, G> singleInputStreamCodec(Function<Boolean, G> creator)
	{
		return StreamCodec.composite(
				ByteBufCodecs.BOOL, LogicGate::output,
				creator
		);
	}
	
	private boolean outputState;
	
	protected LogicGate(LogicGateConfig config, boolean outputState)
	{
		super(config);
		this.outputState = outputState;
	}
	
	protected LogicGate(boolean outputState)
	{
		super(new LogicGateConfig(0));
		this.config.inputs = this.inputsAllowed().min();
		this.outputState = outputState;
	}
	
	protected abstract boolean processInputs(LogicContext context, boolean[] inputs);
	
	@Override
	public final void processTickInternal(LogicContext context, boolean[] inputs)
	{
		super.processTick(context, inputs);
		
		boolean originalOutput = outputState;
		outputState = this.processInputs(context, inputs);
		if(outputState != originalOutput)
		{
			context.flagChanged();
		}
	}
	
	@Override
	public final int inputs()
	{
		return config.inputs;
	}
	
	@Override
	public final IntRange outputsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public final int outputs()
	{
		return 1;
	}
	
	@Override
	public final boolean output(int index)
	{
		return outputState;
	}
	
	public final boolean output()
	{
		return outputState;
	}
	
	@Override
	public void resetForPickup()
	{
		outputState = false;
	}
}
