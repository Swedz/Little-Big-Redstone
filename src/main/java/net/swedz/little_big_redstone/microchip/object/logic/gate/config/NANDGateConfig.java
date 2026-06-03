package net.swedz.little_big_redstone.microchip.object.logic.gate.config;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

import java.util.List;

public final class NANDGateConfig extends MultiLogicGateConfig<NANDGateConfig>
{
	public static final NANDGateConfig DEFAULT = new NANDGateConfig();
	
	public static final MapCodec<NANDGateConfig>             CODEC        = codec(DEFAULT, NANDGateConfig::new);
	public static final StreamCodec<ByteBuf, NANDGateConfig> STREAM_CODEC = streamCodec(NANDGateConfig::new);
	
	public NANDGateConfig(int inputs)
	{
		super(inputs);
	}
	
	public NANDGateConfig()
	{
		super();
	}
	
	@Override
	public LogicType<?, NANDGateConfig> type()
	{
		return LogicTypes.NAND;
	}
	
	@Override
	protected NANDGateConfig mutateConfig(int inputs)
	{
		return new NANDGateConfig(inputs);
	}
	
	@Override
	public void appendNoShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicGateAlgebra(LBR.text().logicGateAlgebraNAND()));
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpNANDGate());
	}
}
