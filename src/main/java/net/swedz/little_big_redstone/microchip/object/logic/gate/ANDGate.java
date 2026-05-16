package net.swedz.little_big_redstone.microchip.object.logic.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.gate.config.MultiLogicGateConfig;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ANDGate extends LogicGate<ANDGate, MultiLogicGateConfig>
{
	public static final MapCodec<ANDGate> CODEC = mapCodec(MultiLogicGateConfig.CODEC, ANDGate::new);
	
	public static final StreamCodec<ByteBuf, ANDGate> STREAM_CODEC = streamCodec(MultiLogicGateConfig.STREAM_CODEC, ANDGate::new);
	
	private ANDGate(MultiLogicGateConfig config, Optional<DyeColor> color, int outputState)
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
	protected MultiLogicGateConfig defaultConfig()
	{
		return new MultiLogicGateConfig();
	}
	
	@Override
	public LogicType<ANDGate> type()
	{
		return LogicTypes.AND;
	}
	
	@Override
	public int processInputs(LogicContext context, int[] inputs)
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
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicGateAlgebra(LBR.text().logicGateAlgebraAND()));
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpANDGate());
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