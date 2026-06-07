package net.swedz.little_big_redstone.microchip.object.logic.gate.config;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;

import java.util.List;

public final class ANDGateConfig extends MultiLogicGateConfig<ANDGateConfig>
{
	public static final ANDGateConfig DEFAULT = new ANDGateConfig();
	
	public static final MapCodec<ANDGateConfig>             CODEC        = codec(ANDGateConfig::new);
	public static final StreamCodec<ByteBuf, ANDGateConfig> STREAM_CODEC = streamCodec(ANDGateConfig::new);
	
	public ANDGateConfig(int inputs)
	{
		super(inputs);
	}
	
	public ANDGateConfig()
	{
		super();
	}
	
	@Override
	public LogicType<?, ANDGateConfig> type()
	{
		return LBRLogicTypes.AND.get();
	}
	
	@Override
	protected ANDGateConfig mutateConfig(int inputs)
	{
		return new ANDGateConfig(inputs);
	}
	
	@Override
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicGateAlgebra(LBR.text().logicGateAlgebraAND()));
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpANDGate());
	}
}
