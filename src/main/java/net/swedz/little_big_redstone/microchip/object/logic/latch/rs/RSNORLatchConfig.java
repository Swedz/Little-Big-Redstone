package net.swedz.little_big_redstone.microchip.object.logic.latch.rs;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.tesseract.neoforge.api.range.IntRange;

import java.util.List;

public record RSNORLatchConfig() implements LogicConfig
{
	public static final RSNORLatchConfig DEFAULT = new RSNORLatchConfig();
	
	public static final MapCodec<RSNORLatchConfig> CODEC = MapCodec.unit(DEFAULT);
	
	public static final StreamCodec<ByteBuf, RSNORLatchConfig> STREAM_CODEC = StreamCodec.unit(DEFAULT);
	
	@Override
	public LogicType type()
	{
		return LBRLogicTypes.RS_NOR_LATCH.get();
	}
	
	@Override
	public IntRange inputPortsAllowed()
	{
		return new IntRange(2, 2);
	}
	
	@Override
	public int inputPorts()
	{
		return 2;
	}
	
	@Override
	public IntRange outputPortsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public int outputPorts()
	{
		return 1;
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpRSNORLatch1());
		lines.add(LBR.text().logicHelpRSNORLatch2());
	}
}