package net.swedz.redstone_circuitry.microchip.logic.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.redstone_circuitry.RCText;
import net.swedz.redstone_circuitry.api.IntRange;
import net.swedz.redstone_circuitry.microchip.logic.LogicContext;
import net.swedz.redstone_circuitry.microchip.logic.LogicType;
import net.swedz.redstone_circuitry.microchip.logic.LogicTypes;

import java.util.List;

import static net.swedz.redstone_circuitry.RCTooltips.*;
import static net.swedz.tesseract.neoforge.tooltip.TextLine.*;

public final class XORGate extends LogicGate<XORGate>
{
	public static final XORGate DEFAULT = new XORGate(false);
	
	public static final MapCodec<XORGate> CODEC = mapCodec(XORGate::new);
	
	public static final StreamCodec<ByteBuf, XORGate> STREAM_CODEC = streamCodec(XORGate::new);
	
	private XORGate(int inputs, boolean outputState)
	{
		super(inputs, outputState);
	}
	
	private XORGate(boolean outputState)
	{
		super(outputState);
	}
	
	@Override
	public LogicType<XORGate> type()
	{
		return LogicTypes.XOR;
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(2, 16);
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
		lines.add(line(RCText.LOGIC_GATE_ALGEBRA).arg(RCText.LOGIC_GATE_ALGEBRA_XOR.text().withStyle(DEFAULT_STYLE)).withStyle(DEFAULT_STYLE));
	}
	
	@Override
	public int hashCode()
	{
		return this.type().hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o;
	}
}
