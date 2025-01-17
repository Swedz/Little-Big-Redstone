package net.swedz.redstone_circuitry.microchip.logic.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.swedz.redstone_circuitry.RCText;
import net.swedz.redstone_circuitry.api.IntRange;
import net.swedz.redstone_circuitry.microchip.logic.LogicContext;
import net.swedz.redstone_circuitry.microchip.logic.LogicType;
import net.swedz.redstone_circuitry.microchip.logic.LogicTypes;

import java.util.List;

import static net.swedz.redstone_circuitry.RCTooltips.*;
import static net.swedz.tesseract.neoforge.tooltip.TextLine.*;

public final class ORGate extends LogicGate<ORGate>
{
	public static final ORGate DEFAULT = new ORGate(false);
	
	public static final MapCodec<ORGate> CODEC = mapCodec(ORGate::new);
	
	public static final StreamCodec<ByteBuf, ORGate> STREAM_CODEC = streamCodec(ORGate::new);
	
	private ORGate(int inputs, boolean outputState)
	{
		super(inputs, outputState);
	}
	
	private ORGate(boolean outputState)
	{
		super(outputState);
	}
	
	@Override
	public LogicType<ORGate> type()
	{
		return LogicTypes.OR;
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(2, 16);
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
