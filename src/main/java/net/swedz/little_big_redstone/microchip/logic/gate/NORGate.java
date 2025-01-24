package net.swedz.little_big_redstone.microchip.logic.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.logic.gate.config.MultiLogicGateConfig;

import java.util.List;
import java.util.Objects;

import static net.swedz.little_big_redstone.LBRTextLine.*;
import static net.swedz.little_big_redstone.LBRTooltips.*;

public final class NORGate extends LogicGate<NORGate, MultiLogicGateConfig>
{
	public static final MapCodec<NORGate> CODEC = mapCodec(MultiLogicGateConfig.CODEC, NORGate::new);
	
	public static final StreamCodec<ByteBuf, NORGate> STREAM_CODEC = streamCodec(MultiLogicGateConfig.STREAM_CODEC, NORGate::new);
	
	private NORGate(MultiLogicGateConfig config, boolean outputState)
	{
		super(config, outputState);
	}
	
	private NORGate(boolean outputState)
	{
		super(outputState);
	}
	
	public NORGate()
	{
		this(false);
	}
	
	@Override
	protected MultiLogicGateConfig defaultConfig()
	{
		return new MultiLogicGateConfig();
	}
	
	@Override
	public LogicType<NORGate> type()
	{
		return LogicTypes.NOR;
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
