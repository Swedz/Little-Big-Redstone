package net.swedz.little_big_redstone.microchip.logic.gate;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.LogicGridSize;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfig;

import java.util.Optional;
import java.util.function.BiFunction;

public abstract class LogicGate<G extends LogicGate<G, C>, C extends LogicConfig<C>> extends LogicComponent<G, C>
{
	protected static <G extends LogicGate<G, C>, C extends LogicConfig<C>> MapCodec<G> mapCodec(Codec<C> configCodec, Function3<C, Optional<DyeColor>, Boolean, G> function)
	{
		return RecordCodecBuilder.mapCodec((instance) -> instance
				.group(
						configCodec.fieldOf("config").forGetter(LogicComponent::config),
						DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicComponent::color),
						Codec.BOOL.fieldOf("output").forGetter(LogicGate::output)
				)
				.apply(instance, function));
	}
	
	protected static <G extends LogicGate<G, C>, C extends LogicConfig<C>> MapCodec<G> mapCodec(BiFunction<Optional<DyeColor>, Boolean, G> creator)
	{
		return RecordCodecBuilder.mapCodec((instance) -> instance
				.group(
						DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicComponent::color),
						Codec.BOOL.fieldOf("output").forGetter(LogicGate::output)
				)
				.apply(instance, creator));
	}
	
	protected static <G extends LogicGate<G, C>, C extends LogicConfig<C>> StreamCodec<ByteBuf, G> streamCodec(StreamCodec<ByteBuf, C> configCodec, Function3<C, Optional<DyeColor>, Boolean, G> function)
	{
		return StreamCodec.composite(
				configCodec, LogicComponent::config,
				ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicGate::color,
				ByteBufCodecs.BOOL, LogicGate::output,
				function
		);
	}
	
	protected static <G extends LogicGate<G, C>, C extends LogicConfig<C>> StreamCodec<ByteBuf, G> streamCodec(BiFunction<Optional<DyeColor>, Boolean, G> creator)
	{
		return StreamCodec.composite(
				ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicGate::color,
				ByteBufCodecs.BOOL, LogicGate::output,
				creator
		);
	}
	
	private boolean outputState;
	
	protected LogicGate(C config, Optional<DyeColor> color, boolean outputState)
	{
		super(config, color);
		this.outputState = outputState;
	}
	
	protected LogicGate(Optional<DyeColor> color, boolean outputState)
	{
		super(color);
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
		return new LogicGridSize(1, Math.max(1, inputs / 2));
	}
	
	@Override
	protected void internalLoadFrom(G other)
	{
		outputState = other.output();
	}
	
	@Override
	public void internalResetForPickup()
	{
		outputState = false;
	}
}
