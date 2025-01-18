package net.swedz.little_big_redstone.microchip.logic.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.api.IntRange;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.LogicFactory;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;

import java.util.List;

import static net.swedz.little_big_redstone.LBRTooltips.*;
import static net.swedz.tesseract.neoforge.tooltip.TextLine.*;

public final class NANDGate extends LogicGate<NANDGate>
{
	public static final LogicFactory DEFAULT = () -> new NANDGate(false);
	
	public static final MapCodec<NANDGate> CODEC = mapCodec(NANDGate::new);
	
	public static final StreamCodec<ByteBuf, NANDGate> STREAM_CODEC = streamCodec(NANDGate::new);
	
	private NANDGate(int inputs, boolean outputState)
	{
		super(inputs, outputState);
	}
	
	private NANDGate(boolean outputState)
	{
		super(outputState);
	}
	
	@Override
	public LogicType<NANDGate> type()
	{
		return LogicTypes.NAND;
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
		lines.add(line(LBRText.LOGIC_GATE_ALGEBRA).arg(LBRText.LOGIC_GATE_ALGEBRA_NAND.text().withStyle(DEFAULT_STYLE)).withStyle(DEFAULT_STYLE));
	}
	
	@Override
	public NANDGate copy()
	{
		return new NANDGate(this.inputs(), this.output());
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
