package net.swedz.little_big_redstone.microchip.object.logic.gate;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTickingContext;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;

import java.util.Optional;
import java.util.function.BiFunction;

public abstract class LogicGate<G extends LogicGate<G, C>, C extends LogicConfig> extends LogicComponent<G, C>
{
	protected static <G extends LogicGate<G, C>, C extends LogicConfig> MapCodec<G> mapCodec(MapCodec<C> configCodec, Function3<C, Optional<DyeColor>, Integer, G> function)
	{
		return RecordCodecBuilder.mapCodec((instance) -> instance
				.group(
						configCodec.fieldOf("config").forGetter(LogicComponent::config),
						DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicComponent::color),
						Codec.intRange(0, 15).optionalFieldOf("output", 0).forGetter(LogicGate::output)
				)
				.apply(instance, function));
	}
	
	protected static <G extends LogicGate<G, C>, C extends LogicConfig> MapCodec<G> mapCodec(BiFunction<Optional<DyeColor>, Integer, G> creator)
	{
		return RecordCodecBuilder.mapCodec((instance) -> instance
				.group(
						DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicComponent::color),
						Codec.intRange(0, 15).optionalFieldOf("output", 0).forGetter(LogicGate::output)
				)
				.apply(instance, creator));
	}
	
	protected static <G extends LogicGate<G, C>, C extends LogicConfig> StreamCodec<ByteBuf, G> streamCodec(StreamCodec<ByteBuf, C> configCodec, Function3<C, Optional<DyeColor>, Integer, G> function)
	{
		return StreamCodec.composite(
				configCodec, LogicComponent::config,
				ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicGate::color,
				ByteBufCodecs.VAR_INT, LogicGate::output,
				function
		);
	}
	
	protected static <G extends LogicGate<G, C>, C extends LogicConfig> StreamCodec<ByteBuf, G> streamCodec(BiFunction<Optional<DyeColor>, Integer, G> creator)
	{
		return StreamCodec.composite(
				ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicGate::color,
				ByteBufCodecs.VAR_INT, LogicGate::output,
				creator
		);
	}
	
	private int outputState;
	
	protected LogicGate(C config, Optional<DyeColor> color, int outputState)
	{
		super(config, color);
		this.outputState = outputState;
	}
	
	protected LogicGate(Optional<DyeColor> color, int outputState)
	{
		super(color);
		this.outputState = outputState;
	}
	
	protected abstract int processInputs(LogicTickingContext context, int[] inputs);
	
	@Override
	public final void processTickInternal(LogicTickingContext context, int[] inputs)
	{
		int originalOutputState = outputState;
		outputState = this.processInputs(context, inputs);
		if(outputState != originalOutputState)
		{
			context.markDirty(this);
		}
	}
	
	@Override
	protected final int outputInternal(int index)
	{
		return outputState;
	}
	
	public final int output()
	{
		return this.output(0);
	}
	
	@Override
	protected void internalLoadFrom(G other)
	{
		outputState = other.output();
	}
}