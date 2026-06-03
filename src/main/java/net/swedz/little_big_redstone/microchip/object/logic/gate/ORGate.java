package net.swedz.little_big_redstone.microchip.object.logic.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTickingContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.gate.config.ORGateConfig;

import java.util.Objects;
import java.util.Optional;

public final class ORGate extends LogicGate<ORGate, ORGateConfig>
{
	public static final MapCodec<ORGate> CODEC = mapCodec(ORGateConfig.CODEC, ORGate::new);
	
	public static final StreamCodec<ByteBuf, ORGate> STREAM_CODEC = streamCodec(ORGateConfig.STREAM_CODEC, ORGate::new);
	
	private ORGate(ORGateConfig config, Optional<DyeColor> color, int outputState)
	{
		super(config, color, outputState);
	}
	
	private ORGate(Optional<DyeColor> color, int outputState)
	{
		super(color, outputState);
	}
	
	public ORGate()
	{
		this(Optional.empty(), 0);
	}
	
	@Override
	public LogicType<ORGate, ORGateConfig> type()
	{
		return LogicTypes.OR;
	}
	
	@Override
	public int processInputs(LogicTickingContext context, int[] inputs)
	{
		int greatest = 0;
		for(int input : inputs)
		{
			if(input > greatest)
			{
				greatest = input;
			}
		}
		return greatest;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.type(), config, color);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof ORGate other && Objects.equals(config, other.config) && Objects.equals(color, other.color));
	}
}
