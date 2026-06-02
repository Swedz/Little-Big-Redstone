package net.swedz.little_big_redstone.microchip.object.logic.gate.config;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

import java.util.List;

public final class XORGateConfig extends MultiLogicGateConfig<XORGateConfig>
{
	public static final XORGateConfig DEFAULT = new XORGateConfig();
	
	public static final MapCodec<XORGateConfig>             CODEC        = codec(XORGateConfig::new);
	public static final StreamCodec<ByteBuf, XORGateConfig> STREAM_CODEC = streamCodec(XORGateConfig::new);
	
	public XORGateConfig(int inputs)
	{
		super(inputs);
	}
	
	public XORGateConfig()
	{
		super();
	}
	
	@Override
	public LogicType<?, XORGateConfig> type()
	{
		return LogicTypes.XOR;
	}
	
	@Override
	protected XORGateConfig mutateConfig(int inputs)
	{
		return new XORGateConfig(inputs);
	}
	
	@Override
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicGateAlgebra(LBR.text().logicGateAlgebraXOR()));
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpXORGate());
	}
}
