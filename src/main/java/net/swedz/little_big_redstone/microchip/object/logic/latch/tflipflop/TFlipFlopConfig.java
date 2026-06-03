package net.swedz.little_big_redstone.microchip.object.logic.latch.tflipflop;

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

public record TFlipFlopConfig() implements LogicConfig<TFlipFlopConfig>
{
	public static final TFlipFlopConfig DEFAULT = new TFlipFlopConfig();
	
	public static final MapCodec<TFlipFlopConfig> CODEC = MapCodec.unit(DEFAULT);
	
	public static final StreamCodec<ByteBuf, TFlipFlopConfig> STREAM_CODEC = StreamCodec.unit(DEFAULT);
	
	@Override
	public LogicType<?, TFlipFlopConfig> type()
	{
		return LogicTypes.T_FLIP_FLOP;
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public int inputs()
	{
		return 1;
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
		lines.add(LBR.text().logicHelpTFlipFlop());
	}
}