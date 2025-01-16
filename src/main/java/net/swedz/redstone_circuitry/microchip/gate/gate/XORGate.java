package net.swedz.redstone_circuitry.microchip.gate.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.swedz.redstone_circuitry.RCText;
import net.swedz.redstone_circuitry.microchip.gate.LogicGate;
import net.swedz.redstone_circuitry.microchip.gate.LogicGateType;
import net.swedz.redstone_circuitry.microchip.gate.LogicGates;

import java.util.List;

import static net.swedz.redstone_circuitry.RCTooltips.*;
import static net.swedz.tesseract.neoforge.tooltip.TextLine.*;

public final class XORGate implements LogicGate<XORGate>
{
	public static final XORGate INSTANCE = new XORGate();
	
	public static final MapCodec<XORGate> CODEC = MapCodec.unit(INSTANCE);
	
	public static final StreamCodec<ByteBuf, XORGate> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	
	private XORGate()
	{
	}
	
	@Override
	public LogicGateType<XORGate> type()
	{
		return LogicGates.XOR;
	}
	
	@Override
	public int inputCount()
	{
		return 3;
	}
	
	@Override
	public boolean process(Level level, BlockPos pos, boolean[] inputs)
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
	public void appendTooltip(Item.TooltipContext context, List<Component> lines)
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
