package net.swedz.little_big_redstone.microchip.object.logic.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTickingContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.gate.config.ANDGateConfig;

import java.util.Objects;
import java.util.Optional;

public final class ANDGate extends LogicGate<ANDGate, ANDGateConfig>
{
	public static final MapCodec<ANDGate> CODEC = mapCodec(ANDGateConfig.CODEC, ANDGate::new);
	
	public static final StreamCodec<ByteBuf, ANDGate> STREAM_CODEC = streamCodec(ANDGateConfig.STREAM_CODEC, ANDGate::new);
	
	private ANDGate(ANDGateConfig config, Optional<DyeColor> color, int outputState)
	{
		super(config, color, outputState);
	}
	
	private ANDGate(Optional<DyeColor> color, int outputState)
	{
		super(color, outputState);
	}
	
	public ANDGate()
	{
		this(Optional.empty(), 0);
	}
	
	@Override
	public LogicType type()
	{
		return LBRLogicTypes.AND.get();
	}
	
	@Override
	public int processInputs(LogicTickingContext context, int[] inputs)
	{
		int greatest = 0;
		for(int input : inputs)
		{
			if(input == 0)
			{
				return 0;
			}
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
			   (o instanceof ANDGate other && Objects.equals(config, other.config) && Objects.equals(color, other.color));
	}
}