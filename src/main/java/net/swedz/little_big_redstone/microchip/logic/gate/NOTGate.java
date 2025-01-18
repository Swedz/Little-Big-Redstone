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

public final class NOTGate extends LogicGate<NOTGate>
{
	public static final LogicFactory DEFAULT = () -> new NOTGate(false);
	
	public static final MapCodec<NOTGate> CODEC = singleInputMapCodec(NOTGate::new);
	
	public static final StreamCodec<ByteBuf, NOTGate> STREAM_CODEC = singleInputStreamCodec(NOTGate::new);
	
	private NOTGate(boolean outputState)
	{
		super(outputState);
	}
	
	@Override
	public LogicType<NOTGate> type()
	{
		return LogicTypes.NOT;
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public boolean processInputs(LogicContext context, boolean[] inputs)
	{
		return !inputs[0];
	}
	
	@Override
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_GATE_ALGEBRA).arg(LBRText.LOGIC_GATE_ALGEBRA_NOT.text().withStyle(DEFAULT_STYLE)).withStyle(DEFAULT_STYLE));
	}
	
	@Override
	public NOTGate copy()
	{
		return new NOTGate(this.output());
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
