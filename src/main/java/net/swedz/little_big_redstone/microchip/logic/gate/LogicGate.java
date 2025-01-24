package net.swedz.little_big_redstone.microchip.logic.gate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.LogicGridSize;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfig;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class LogicGate<G extends LogicGate<G, C>, C extends LogicConfig<C>> extends LogicComponent<G, C>
{
	protected static <G extends LogicGate<G, C>, C extends LogicConfig<C>> MapCodec<G> mapCodec(Codec<C> configCodec, BiFunction<C, Boolean, G> creator)
	{
		return RecordCodecBuilder.mapCodec((instance) -> instance
				.group(
						configCodec.fieldOf("config").forGetter(LogicComponent::config),
						Codec.BOOL.fieldOf("output").forGetter(LogicGate::output)
				)
				.apply(instance, creator));
	}
	
	protected static <G extends LogicGate<G, C>, C extends LogicConfig<C>> MapCodec<G> mapCodec(Function<Boolean, G> creator)
	{
		return RecordCodecBuilder.mapCodec((instance) -> instance
				.group(
						Codec.BOOL.fieldOf("output").forGetter(LogicGate::output)
				)
				.apply(instance, creator));
	}
	
	protected static <G extends LogicGate<G, C>, C extends LogicConfig<C>> StreamCodec<ByteBuf, G> streamCodec(StreamCodec<ByteBuf, C> configCodec, BiFunction<C, Boolean, G> creator)
	{
		return StreamCodec.composite(
				configCodec, LogicComponent::config,
				ByteBufCodecs.BOOL, LogicGate::output,
				creator
		);
	}
	
	protected static <G extends LogicGate<G, C>, C extends LogicConfig<C>> StreamCodec<ByteBuf, G> streamCodec(Function<Boolean, G> creator)
	{
		return StreamCodec.composite(
				ByteBufCodecs.BOOL, LogicGate::output,
				creator
		);
	}
	
	private boolean outputState;
	
	protected LogicGate(C config, boolean outputState)
	{
		super(config);
		this.outputState = outputState;
	}
	
	protected LogicGate(boolean outputState)
	{
		super();
		this.outputState = outputState;
	}
	
	protected abstract boolean processInputs(LogicContext context, boolean[] inputs);
	
	@Override
	public final void processTickInternal(LogicContext context, boolean[] inputs)
	{
		boolean originalOutput = outputState;
		outputState = this.processInputs(context, inputs);
		if(outputState != originalOutput)
		{
			context.markDirty(this);
		}
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
	public LogicGridSize size()
	{
		int inputs = this.inputs();
		return new LogicGridSize(1, inputs >= 2 ? inputs / 2 : 1);
	}
	
	@Override
	protected void internalLoadFrom(G other)
	{
		outputState = other.output();
	}
	
	@Override
	public void resetForPickup()
	{
		outputState = false;
	}
}
