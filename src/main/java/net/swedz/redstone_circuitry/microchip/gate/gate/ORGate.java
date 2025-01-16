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

public final class ORGate implements LogicGate<ORGate>
{
	public static final ORGate INSTANCE = new ORGate();
	
	public static final MapCodec<ORGate> CODEC = MapCodec.unit(INSTANCE);
	
	public static final StreamCodec<ByteBuf, ORGate> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	
	private ORGate()
	{
	}
	
	@Override
	public LogicGateType<ORGate> type()
	{
		return LogicGates.OR;
	}
	
	@Override
	public int inputCount()
	{
		return 3;
	}
	
	@Override
	public boolean process(Level level, BlockPos pos, boolean[] inputs)
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
	public void appendTooltip(Item.TooltipContext context, List<Component> lines)
	{
		lines.add(line(RCText.LOGIC_GATE_ALGEBRA).arg(RCText.LOGIC_GATE_ALGEBRA_OR.text().withStyle(DEFAULT_STYLE)).withStyle(DEFAULT_STYLE));
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
