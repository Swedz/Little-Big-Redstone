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

public final class NOTGate extends LogicGate<NOTGate>
{
	public static final NOTGate INSTANCE = new NOTGate();
	
	public static final MapCodec<NOTGate> CODEC = MapCodec.unit(INSTANCE);
	
	public static final StreamCodec<ByteBuf, NOTGate> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	
	private NOTGate()
	{
	}
	
	@Override
	public LogicType<NOTGate> type()
	{
		return Logics.NOT;
	}
	
	@Override
	public IntRange inputs()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public boolean processInputs(Level level, BlockPos pos, boolean[] inputs)
	{
		return !inputs[0];
	}
	
	@Override
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(line(RCText.LOGIC_GATE_ALGEBRA).arg(RCText.LOGIC_GATE_ALGEBRA_NOT.text().withStyle(DEFAULT_STYLE)).withStyle(DEFAULT_STYLE));
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
