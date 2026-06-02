package net.swedz.little_big_redstone.microchip.object.logic.latch.rs;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.tesseract.neoforge.api.range.IntRange;

import java.util.List;

public record RSNORLatchConfig() implements LogicConfig<RSNORLatchConfig>
{
	public static final RSNORLatchConfig DEFAULT = new RSNORLatchConfig();
	
	public static final MapCodec<RSNORLatchConfig> CODEC = MapCodec.unit(DEFAULT);
	
	public static final StreamCodec<ByteBuf, RSNORLatchConfig> STREAM_CODEC = StreamCodec.unit(DEFAULT);
	
	@Override
	public LogicType<?, RSNORLatchConfig> type()
	{
		return LogicTypes.RS_NOR_LATCH;
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(2, 2);
	}
	
	@Override
	public int inputs()
	{
		return 2;
	}
	
	@Override
	public IntRange outputsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public int outputs()
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