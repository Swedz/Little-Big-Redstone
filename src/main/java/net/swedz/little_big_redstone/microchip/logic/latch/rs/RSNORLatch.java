package net.swedz.little_big_redstone.microchip.logic.latch.rs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;

import java.util.List;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public final class RSNORLatch extends LogicComponent<RSNORLatch, RSNORLatchConfig>
{
	public static final MapCodec<RSNORLatch> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.BOOL.fieldOf("output").forGetter(RSNORLatch::output)
			)
			.apply(instance, RSNORLatch::new));
	
	public static final StreamCodec<ByteBuf, RSNORLatch> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, RSNORLatch::output,
			RSNORLatch::new
	);
	
	private boolean outputState;
	
	private RSNORLatch(boolean outputState)
	{
		this.outputState = outputState;
	}
	
	public RSNORLatch()
	{
		this(false);
	}
	
	@Override
	protected RSNORLatchConfig defaultConfig()
	{
		return RSNORLatchConfig.INSTANCE;
	}
	
	@Override
	public LogicType<RSNORLatch> type()
	{
		return LogicTypes.RS_NOR_LATCH;
	}
	
	@Override
	protected void processTickInternal(LogicContext context, boolean[] inputs)
	{
		boolean set = inputs[0];
		boolean reset = inputs[1];
		
		if(reset)
		{
			outputState = false;
		}
		else if(set)
		{
			outputState = true;
		}
	}
	
	@Override
	public boolean output(int index)
	{
		return outputState;
	}
	
	public boolean output()
	{
		return outputState;
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_HELP_RS_NOR_LATCH_1));
		lines.add(line(LBRText.LOGIC_HELP_RS_NOR_LATCH_2));
	}
	
	@Override
	protected void internalLoadFrom(RSNORLatch other)
	{
		outputState = other.outputState;
	}
	
	@Override
	protected void internalResetForPickup()
	{
		outputState = false;
	}
	
	@Override
	public RSNORLatch copy()
	{
		return new RSNORLatch(outputState);
	}
	
	@Override
	public int hashCode()
	{
		return this.type().hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof RSNORLatch;
	}
}
