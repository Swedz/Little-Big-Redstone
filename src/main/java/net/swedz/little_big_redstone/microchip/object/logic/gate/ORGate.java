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

public final class ORGate extends LogicGate<ORGate, MultiLogicGateConfig>
{
	public static final MapCodec<ORGate> CODEC = mapCodec(MultiLogicGateConfig.CODEC, ORGate::new);
	
	public static final StreamCodec<ByteBuf, ORGate> STREAM_CODEC = streamCodec(MultiLogicGateConfig.STREAM_CODEC, ORGate::new);
	
	private ORGate(MultiLogicGateConfig config, Optional<DyeColor> color, boolean outputState)
	{
		super(config, color, outputState);
	}
	
	private ORGate(Optional<DyeColor> color, boolean outputState)
	{
		super(color, outputState);
	}
	
	public ORGate()
	{
		this(Optional.empty(), false);
	}
	
	@Override
	protected MultiLogicGateConfig defaultConfig()
	{
		return new MultiLogicGateConfig();
	}
	
	@Override
	public LogicType<ORGate> type()
	{
		return LogicTypes.OR;
	}
	
	@Override
	public boolean processInputs(LogicContext context, boolean[] inputs)
	{
		for(boolean input : inputs)
		{
			if(input)
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicGateAlgebra(LBR.text().logicGateAlgebraOR()));
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpORGate());
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
