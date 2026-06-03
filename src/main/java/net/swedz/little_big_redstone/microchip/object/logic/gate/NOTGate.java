package net.swedz.little_big_redstone.microchip.object.logic.gate;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTickingContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.gate.config.NOTGateConfig;

import java.util.Objects;
import java.util.Optional;

public final class NOTGate extends LogicGate<NOTGate, NOTGateConfig>
{
	public static final MapCodec<NOTGate> CODEC = mapCodec(NOTGate::new);
	
	public static final StreamCodec<ByteBuf, NOTGate> STREAM_CODEC = streamCodec(NOTGate::new);
	
	private NOTGate(Optional<DyeColor> color, int outputState)
	{
		super(color, outputState);
	}
	
	public NOTGate()
	{
		this(Optional.empty(), 0);
	}
	
	@Override
	public LogicType<NOTGate, NOTGateConfig> type()
	{
		return LogicTypes.NOT;
	}
	
	@Override
	public int processInputs(LogicTickingContext context, int[] inputs)
	{
		return inputs[0] == 0 ? 1 : 0;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.type(), color);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof NOTGate other && Objects.equals(color, other.color));
	}
}
