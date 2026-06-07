package net.swedz.little_big_redstone.microchip.object.logic.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTickingContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.gate.config.XORGateConfig;

import java.util.Objects;
import java.util.Optional;

public final class XORGate extends LogicGate<XORGate, XORGateConfig>
{
	public static final MapCodec<XORGate> CODEC = mapCodec(XORGateConfig.CODEC, XORGate::new);
	
	public static final StreamCodec<ByteBuf, XORGate> STREAM_CODEC = streamCodec(XORGateConfig.STREAM_CODEC, XORGate::new);
	
	private XORGate(XORGateConfig config, Optional<DyeColor> color, int outputState)
	{
		super(config, color, outputState);
	}
	
	private XORGate(Optional<DyeColor> color, int outputState)
	{
		super(color, outputState);
	}
	
	public XORGate()
	{
		this(Optional.empty(), 0);
	}
	
	@Override
	public LogicType type()
	{
		return LBRLogicTypes.XOR.get();
	}
	
	@Override
	public int processInputs(LogicTickingContext context, int[] inputs)
	{
		int trueCount = 0;
		int greatest = 0;
		for(int input : inputs)
		{
			if(input > 0)
			{
				trueCount++;
			}
			if(input > greatest)
			{
				greatest = input;
			}
		}
		return trueCount % 2 != 0 ? greatest : 0;
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
			   (o instanceof XORGate other && Objects.equals(config, other.config) && Objects.equals(color, other.color));
	}
}
