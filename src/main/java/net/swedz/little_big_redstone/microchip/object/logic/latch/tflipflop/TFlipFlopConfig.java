package net.swedz.little_big_redstone.microchip.object.logic.latch.tflipflop;

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

public record TFlipFlopConfig() implements LogicConfig
{
	public static final TFlipFlopConfig DEFAULT = new TFlipFlopConfig();
	
	public static final MapCodec<TFlipFlopConfig>             CODEC        = MapCodec.unit(DEFAULT);
	public static final StreamCodec<ByteBuf, TFlipFlopConfig> STREAM_CODEC = StreamCodec.unit(DEFAULT);
	
	@Override
	public LogicType type()
	{
		return LBRLogicTypes.T_FLIP_FLOP.get();
	}
	
	@Override
	public IntRange inputPortsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public int inputPorts()
	{
		return 1;
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
		lines.add(LBR.text().logicHelpTFlipFlop());
	}
}