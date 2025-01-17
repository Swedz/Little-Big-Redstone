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

public final class NANDGate extends LogicGate<NANDGate>
{
	public static final NANDGate INSTANCE = new NANDGate();
	
	public static final MapCodec<NANDGate> CODEC = MapCodec.unit(INSTANCE);
	
	public static final StreamCodec<ByteBuf, NANDGate> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	
	private NANDGate()
	{
	}
	
	@Override
	public LogicType<NANDGate> type()
	{
		return Logics.NAND;
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
			if(!input)
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(line(RCText.LOGIC_GATE_ALGEBRA).arg(RCText.LOGIC_GATE_ALGEBRA_NAND.text().withStyle(DEFAULT_STYLE)).withStyle(DEFAULT_STYLE));
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
