package net.swedz.redstone_circuitry.microchip.logic.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.swedz.redstone_circuitry.RCText;
import net.swedz.redstone_circuitry.api.IntRange;
import net.swedz.redstone_circuitry.microchip.logic.LogicType;
import net.swedz.redstone_circuitry.microchip.logic.Logics;

import java.util.List;

import static net.swedz.redstone_circuitry.RCTooltips.*;
import static net.swedz.tesseract.neoforge.tooltip.TextLine.*;

public final class ORGate extends LogicGate<ORGate>
{
	public static final ORGate INSTANCE = new ORGate();
	
	public static final MapCodec<ORGate> CODEC = MapCodec.unit(INSTANCE);
	
	public static final StreamCodec<ByteBuf, ORGate> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	
	private ORGate()
	{
	}
	
	@Override
	public LogicType<ORGate> type()
	{
		return Logics.OR;
	}
	
	@Override
	public IntRange inputs()
	{
		return new IntRange(2, 16);
	}
	
	@Override
	public boolean processInputs(Level level, BlockPos pos, boolean[] inputs)
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
