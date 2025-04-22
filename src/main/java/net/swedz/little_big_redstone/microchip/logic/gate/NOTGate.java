package net.swedz.little_big_redstone.microchip.logic.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.logic.gate.config.SingleLogicGateConfig;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public final class NOTGate extends LogicGate<NOTGate, SingleLogicGateConfig>
{
	public static final MapCodec<NOTGate> CODEC = mapCodec(NOTGate::new);
	
	public static final StreamCodec<ByteBuf, NOTGate> STREAM_CODEC = streamCodec(NOTGate::new);
	
	private NOTGate(Optional<DyeColor> color, boolean outputState)
	{
		super(color, outputState);
	}
	
	public NOTGate()
	{
		this(Optional.empty(), false);
	}
	
	@Override
	protected SingleLogicGateConfig defaultConfig()
	{
		return SingleLogicGateConfig.INSTANCE;
	}
	
	@Override
	public LogicType<NOTGate> type()
	{
		return LogicTypes.NOT;
	}
	
	@Override
	public boolean processInputs(LogicContext context, boolean[] inputs)
	{
		return !inputs[0];
	}
	
	@Override
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_GATE_ALGEBRA).arg(LBRText.LOGIC_GATE_ALGEBRA_NOT.text()));
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_HELP_NOT_GATE));
	}
	
	@Override
	public NOTGate copy()
	{
		return new NOTGate(color, this.output());
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.type(), color);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof NOTGate other && Objects.equals(color, other.color));
	}
}
