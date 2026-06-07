package net.swedz.little_big_redstone.microchip.object.logic.gate.config;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;

import java.util.List;

public record NOTGateConfig() implements SingleLogicGateConfig<NOTGateConfig>
{
	public static final NOTGateConfig                       DEFAULT      = new NOTGateConfig();
	public static final MapCodec<NOTGateConfig>             CODEC        = MapCodec.unit(DEFAULT);
	public static final StreamCodec<ByteBuf, NOTGateConfig> STREAM_CODEC = StreamCodec.unit(DEFAULT);
	
	@Override
	public LogicType<?, NOTGateConfig> type()
	{
		return LBRLogicTypes.NOT.get();
	}
	
	@Override
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicGateAlgebra(LBR.text().logicGateAlgebraNOT()));
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpNOTGate());
	}
}
