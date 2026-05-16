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

public final class NANDGate extends LogicGate<NANDGate, MultiLogicGateConfig>
{
	public static final MapCodec<NANDGate> CODEC = mapCodec(MultiLogicGateConfig.CODEC, NANDGate::new);
	
	public static final StreamCodec<ByteBuf, NANDGate> STREAM_CODEC = streamCodec(MultiLogicGateConfig.STREAM_CODEC, NANDGate::new);
	
	private NANDGate(MultiLogicGateConfig config, Optional<DyeColor> color, int outputState)
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
	protected MultiLogicGateConfig defaultConfig()
	{
		return new MultiLogicGateConfig();
	}
	
	@Override
	public LogicType<NANDGate> type()
	{
		return LogicTypes.NAND;
	}
	
	@Override
	public int processInputs(LogicContext context, int[] inputs)
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
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicGateAlgebra(LBR.text().logicGateAlgebraNAND()));
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpNANDGate());
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
