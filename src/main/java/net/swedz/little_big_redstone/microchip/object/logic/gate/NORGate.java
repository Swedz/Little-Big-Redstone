package net.swedz.little_big_redstone.microchip.object.logic.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTickingContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.gate.config.NORGateConfig;

import java.util.Objects;
import java.util.Optional;

public final class NORGate extends LogicGate<NORGate, NORGateConfig>
{
	public static final MapCodec<NORGate> CODEC = mapCodec(NORGateConfig.CODEC, NORGate::new);
	
	public static final StreamCodec<ByteBuf, NORGate> STREAM_CODEC = streamCodec(NORGateConfig.STREAM_CODEC, NORGate::new);
	
	private NORGate(NORGateConfig config, Optional<DyeColor> color, int outputState)
	{
		super(config, color, outputState);
	}
	
	private NORGate(Optional<DyeColor> color, int outputState)
	{
		super(color, outputState);
	}
	
	public NORGate()
	{
		this(Optional.empty(), 0);
	}
	
	@Override
	public LogicType<NORGate, NORGateConfig> type()
	{
		return LBRLogicTypes.NOR.get();
	}
	
	@Override
	public int processInputs(LogicTickingContext context, int[] inputs)
	{
		for(int input : inputs)
		{
			if(input > 0)
			{
				return 0;
			}
		}
		return 1;
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
			   (o instanceof NORGate other && Objects.equals(config, other.config) && Objects.equals(color, other.color));
	}
}
