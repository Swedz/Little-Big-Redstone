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

public final class ANDGate implements LogicGate<ANDGate>
{
	public static final ANDGate INSTANCE = new ANDGate();
	
	public static final MapCodec<ANDGate> CODEC = MapCodec.unit(INSTANCE);
	
	public static final StreamCodec<ByteBuf, ANDGate> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	
	private ANDGate()
	{
	}
	
	@Override
	public LogicGateType<ANDGate> type()
	{
		return LogicGates.AND;
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
			if(!input)
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void appendTooltip(Item.TooltipContext context, List<Component> lines)
	{
		lines.add(line(RCText.LOGIC_GATE_ALGEBRA).arg(RCText.LOGIC_GATE_ALGEBRA_AND.text().withStyle(DEFAULT_STYLE)).withStyle(DEFAULT_STYLE));
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
