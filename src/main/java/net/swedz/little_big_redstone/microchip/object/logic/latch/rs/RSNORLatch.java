package net.swedz.little_big_redstone.microchip.object.logic.latch.rs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTickingContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;

import java.util.Objects;
import java.util.Optional;

public final class RSNORLatch extends LogicComponent<RSNORLatch, RSNORLatchConfig>
{
	public static final MapCodec<RSNORLatch> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					DyeColor.CODEC.optionalFieldOf("color").forGetter(RSNORLatch::color),
					Codec.INT.optionalFieldOf("output", 0).forGetter(RSNORLatch::output)
			)
			.apply(instance, RSNORLatch::new));
	
	public static final StreamCodec<ByteBuf, RSNORLatch> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), RSNORLatch::color,
			ByteBufCodecs.VAR_INT, RSNORLatch::output,
			RSNORLatch::new
	);
	
	private int outputState;
	
	private RSNORLatch(Optional<DyeColor> color, int outputState)
	{
		super(color);
		this.outputState = outputState;
	}
	
	public RSNORLatch()
	{
		this(Optional.empty(), 0);
	}
	
	@Override
	public LogicType<RSNORLatch, RSNORLatchConfig> type()
	{
		return LBRLogicTypes.RS_NOR_LATCH.get();
	}
	
	@Override
	protected void processTickInternal(LogicTickingContext context, int[] inputs)
	{
		int originalOutputState = outputState;
		int set = inputs[0];
		int reset = inputs[1];
		
		if(reset > 0)
		{
			outputState = 0;
		}
		else if(set > 0)
		{
			outputState = set;
		}
		
		if(originalOutputState != outputState)
		{
			context.markDirty(this);
		}
	}
	
	@Override
	protected int outputInternal(int index)
	{
		return outputState;
	}
	
	public int output()
	{
		return this.output(0);
	}
	
	@Override
	protected void internalLoadFrom(RSNORLatch other)
	{
		outputState = other.outputState;
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
			   (o instanceof RSNORLatch other && Objects.equals(color, other.color));
	}
}