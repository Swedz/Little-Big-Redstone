package net.swedz.little_big_redstone.microchip.object.logic.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.object.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.gate.config.MultiLogicGateConfig;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public final class XORGate extends LogicGate<XORGate, MultiLogicGateConfig>
{
	public static final MapCodec<XORGate> CODEC = mapCodec(MultiLogicGateConfig.CODEC, XORGate::new);
	
	public static final StreamCodec<ByteBuf, XORGate> STREAM_CODEC = streamCodec(MultiLogicGateConfig.STREAM_CODEC, XORGate::new);
	
	private XORGate(MultiLogicGateConfig config, Optional<DyeColor> color, boolean outputState)
	{
		super(config, color, outputState);
	}
	
	private XORGate(Optional<DyeColor> color, boolean outputState)
	{
		super(color, outputState);
	}
	
	public XORGate()
	{
		this(Optional.empty(), false);
	}
	
	@Override
	protected MultiLogicGateConfig defaultConfig()
	{
		return new MultiLogicGateConfig();
	}
	
	@Override
	public LogicType<XORGate> type()
	{
		return LogicTypes.XOR;
	}
	
	@Override
	public boolean processInputs(LogicContext context, boolean[] inputs)
	{
		int trueCount = 0;
		for(boolean input : inputs)
		{
			if(input)
			{
				trueCount++;
			}
		}
		return trueCount % 2 != 0;
	}
	
	@Override
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_GATE_ALGEBRA).arg(LBRText.LOGIC_GATE_ALGEBRA_XOR.text()));
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_HELP_XOR_GATE));
	}
	
	@Override
	public XORGate copy()
	{
		return new XORGate(config.copy(), color, this.output());
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
