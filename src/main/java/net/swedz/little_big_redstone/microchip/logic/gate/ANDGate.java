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

public final class ANDGate extends LogicGate<ANDGate>
{
	public static final LogicFactory DEFAULT = () -> new ANDGate(false);
	
	public static final MapCodec<ANDGate> CODEC = mapCodec(ANDGate::new);
	
	public static final StreamCodec<ByteBuf, ANDGate> STREAM_CODEC = streamCodec(ANDGate::new);
	
	private ANDGate(int inputs, boolean outputState)
	{
		super(inputs, outputState);
	}
	
	private ANDGate(boolean outputState)
	{
		super(outputState);
	}
	
	@Override
	public LogicType<ANDGate> type()
	{
		return LogicTypes.AND;
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
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_GATE_ALGEBRA).arg(LBRText.LOGIC_GATE_ALGEBRA_AND.text().withStyle(DEFAULT_STYLE)).withStyle(DEFAULT_STYLE));
	}
	
	@Override
	public ANDGate copy()
	{
		return new ANDGate(this.inputs(), this.output());
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
