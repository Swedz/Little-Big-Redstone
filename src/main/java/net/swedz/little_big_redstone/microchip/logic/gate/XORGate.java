package net.swedz.little_big_redstone.microchip.logic.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.logic.gate.config.MultiLogicGateConfig;

import java.util.List;
import java.util.Objects;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public final class XORGate extends LogicGate<XORGate, MultiLogicGateConfig>
{
	public static final MapCodec<XORGate> CODEC = mapCodec(MultiLogicGateConfig.CODEC, XORGate::new);
	
	public static final StreamCodec<ByteBuf, XORGate> STREAM_CODEC = streamCodec(MultiLogicGateConfig.STREAM_CODEC, XORGate::new);
	
	private XORGate(MultiLogicGateConfig config, boolean outputState)
	{
		super(config, outputState);
	}
	
	private XORGate(boolean outputState)
	{
		super(outputState);
	}
	
	public XORGate()
	{
		this(false);
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
	public XORGate copy()
	{
		return new XORGate(config.copy(), this.output());
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.type(), config);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof XORGate other && Objects.equals(config, other.config));
	}
}
