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
import java.util.Objects;

import static net.swedz.little_big_redstone.LBRTooltips.*;
import static net.swedz.tesseract.neoforge.tooltip.TextLine.*;

public final class NORGate extends LogicGate<NORGate>
{
	public static final LogicFactory DEFAULT = () -> new NORGate(false);
	
	public static final MapCodec<NORGate> CODEC = mapCodec(NORGate::new);
	
	public static final StreamCodec<ByteBuf, NORGate> STREAM_CODEC = streamCodec(NORGate::new);
	
	private NORGate(LogicGateConfig config, boolean outputState)
	{
		super(config, outputState);
	}
	
	private NORGate(boolean outputState)
	{
		super(outputState);
	}
	
	@Override
	public LogicType<NORGate> type()
	{
		return LogicTypes.NOR;
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
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_GATE_ALGEBRA).arg(LBRText.LOGIC_GATE_ALGEBRA_NOR.text().withStyle(DEFAULT_STYLE)).withStyle(DEFAULT_STYLE));
	}
	
	@Override
	public NORGate copy()
	{
		return new NORGate(config.copy(), this.output());
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.type(), config);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof NORGate other && Objects.equals(config, other.config));
	}
}
