package net.swedz.little_big_redstone.microchip.object.logic.gate.config;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;

import java.util.List;

public final class ORGateConfig extends MultiLogicGateConfig<ORGateConfig>
{
	public static final ORGateConfig DEFAULT = new ORGateConfig();
	
	public static final MapCodec<ORGateConfig>             CODEC        = codec(ORGateConfig::new);
	public static final StreamCodec<ByteBuf, ORGateConfig> STREAM_CODEC = streamCodec(ORGateConfig::new);
	
	public ORGateConfig(int inputs)
	{
		super(inputs);
	}
	
	public ORGateConfig()
	{
		super();
	}
	
	@Override
	public LogicType type()
	{
		return LBRLogicTypes.OR.get();
	}
	
	@Override
	protected ORGateConfig mutateConfig(int inputs)
	{
		return new ORGateConfig(inputs);
	}
	
	@Override
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicGateAlgebra(LBR.text().logicGateAlgebraOR()));
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpORGate());
	}
}
