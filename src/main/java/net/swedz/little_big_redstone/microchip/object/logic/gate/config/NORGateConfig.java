package net.swedz.little_big_redstone.microchip.object.logic.gate.config;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

import java.util.List;

public final class NORGateConfig extends MultiLogicGateConfig<NORGateConfig>
{
	public static final NORGateConfig DEFAULT = new NORGateConfig();
	
	public static final MapCodec<NORGateConfig>             CODEC        = codec(NORGateConfig::new);
	public static final StreamCodec<ByteBuf, NORGateConfig> STREAM_CODEC = streamCodec(NORGateConfig::new);
	
	public NORGateConfig(int inputs)
	{
		super(inputs);
	}
	
	public NORGateConfig()
	{
		super();
	}
	
	@Override
	public LogicType<?, NORGateConfig> type()
	{
		return LogicTypes.NOR;
	}
	
	@Override
	protected NORGateConfig mutateConfig(int inputs)
	{
		return new NORGateConfig(inputs);
	}
	
	@Override
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicGateAlgebra(LBR.text().logicGateAlgebraNOR()));
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpNORGate());
	}
}
