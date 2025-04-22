package net.swedz.little_big_redstone.microchip.logic.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.logic.gate.config.MultiLogicGateConfig;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.swedz.little_big_redstone.LBRTextLine.*;

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
		lines.add(line(LBRText.LOGIC_GATE_ALGEBRA).arg(LBRText.LOGIC_GATE_ALGEBRA_OR.text()));
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_HELP_OR_GATE));
	}
	
	@Override
	public ORGate copy()
	{
		return new ORGate(config.copy(), color, this.output());
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
