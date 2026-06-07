package net.swedz.little_big_redstone.microchip.object.logic.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTickingContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.gate.config.NANDGateConfig;

import java.util.Objects;
import java.util.Optional;

public final class NANDGate extends LogicGate<NANDGate, NANDGateConfig>
{
	public static final MapCodec<NANDGate> CODEC = mapCodec(NANDGateConfig.CODEC, NANDGate::new);
	
	public static final StreamCodec<ByteBuf, NANDGate> STREAM_CODEC = streamCodec(NANDGateConfig.STREAM_CODEC, NANDGate::new);
	
	private NANDGate(NANDGateConfig config, Optional<DyeColor> color, int outputState)
	{
		super(config, color, outputState);
	}
	
	private NANDGate(Optional<DyeColor> color, int outputState)
	{
		super(color, outputState);
	}
	
	public NANDGate()
	{
		this(Optional.empty(), 0);
	}
	
	@Override
	public LogicType type()
	{
		return LBRLogicTypes.NAND.get();
	}
	
	@Override
	public int processInputs(LogicTickingContext context, int[] inputs)
	{
		for(int input : inputs)
		{
			if(input == 0)
			{
				return 1;
			}
		}
		return 0;
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
			   (o instanceof NANDGate other && Objects.equals(config, other.config) && Objects.equals(color, other.color));
	}
}
