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

public final class NORGate extends LogicGate<NORGate, MultiLogicGateConfig>
{
	public static final MapCodec<NORGate> CODEC = mapCodec(MultiLogicGateConfig.CODEC, NORGate::new);
	
	public static final StreamCodec<ByteBuf, NORGate> STREAM_CODEC = streamCodec(MultiLogicGateConfig.STREAM_CODEC, NORGate::new);
	
	private NORGate(MultiLogicGateConfig config, Optional<DyeColor> color, int outputState)
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
	protected MultiLogicGateConfig defaultConfig()
	{
		return new MultiLogicGateConfig();
	}
	
	@Override
	public LogicType<NORGate> type()
	{
		return LogicTypes.NOR;
	}
	
	@Override
	public int processInputs(LogicContext context, int[] inputs)
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
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicGateAlgebra(LBR.text().logicGateAlgebraNOR()));
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpNORGate());
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
